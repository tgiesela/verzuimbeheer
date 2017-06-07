package com.gieselaar.transactieparsers;

/*
	 * Copyright (C) 2008 Arnout Engelen
	 *
	 * This program is free software: you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation, either version 3 of the License, or
	 * (at your option) any later version.
	 *
	 *  This program is distributed in the hope that it will be useful,
	 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
	 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 *  GNU General Public License for more details.
	 *
	 *  You should have received a copy of the GNU General Public License
	 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
	 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gieselaar.transactieparsers.RaboCSVEntry.DebitCreditIndicator;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
	
public class RaboCVSParser {

	private enum importresult {
		werkgevernotfound, fieldsmissing, requiredfieldmissing, invalidlength, formaterror, invalidtype, invaliddate, updaterror, emptyrow, ok, warning
	};

	private interface kolom {
		public int getValue();
		public String toString();
	}

	private enum importkolommen implements kolom {
		rekeningDVV(1) {
			public String toString() {
				return "eigenRekeningnummer";
			}
		},
		muntsoort(2) {
			public String toString() {
				return "muntsoort";
			}
		},
		renteDatum(3) {
			public String toString() {
				return "rentedatum";
			}
		},
		byAfCode(4) {
			public String toString() {
				return "byafcode";
			}
		},
		bedrag(5) {
			public String toString() {
				return "bedrag";
			}
		},
		tegenRekening(6) {
			public String toString() {
				return "tegenrekening";
			}
		},
		naamRekeningHouder(7) {
			public String toString() {
				return "naamrekeninghouder";
			}
		},
		boekDatum(8) {
			public String toString() {
				return "boekdatum";
			}
		},
		boekCode(9) {
			public String toString() {
				return "boekcode";
			}
		},
		rfu1(10) {
			public String toString() {
				return "filler";
			}
		},
		omschr1(11) {
			public String toString() {
				return "omschrijving1";
			}
		},
		omschr2(12) {
			public String toString() {
				return "omschrijving2";
			}
		},
		omschr3(13) {
			public String toString() {
				return "omschrijving3";
			}
		},
		omschr4(14) {
			public String toString() {
				return "omschrijving4";
			}
		},
		omschr5(15) {
			public String toString() {
				return "omschrijving5";
			}
		},
		omschr6(16) {
			public String toString() {
				return "omschrijving6";
			}
		},
		endToEndId(17) {
			public String toString() {
				return "endtoendid";
			}
		},
		idTegenRekHouder(18) {
			public String toString() {
				return "idtegenrekeninghouder";
			}
		},
		mandaatId(19) {
			public String toString() {
				return "mandaatid";
			}
		};

		private int value;

		importkolommen(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@SuppressWarnings("unused")
		public static importkolommen parse(Integer id) {
			importkolommen soort = null; // Default
			for (importkolommen item : importkolommen.values()) {
				if (item.getValue() == id) {
					soort = item;
					break;
				}
			}
			return soort;
		}
	}

	private class parseTable {
		kolom kolom;
		String formaat;
		int minlen;
		int maxlen;
		boolean verplicht;
		String type;

		private void storevalues(kolom kolom, String type, int minlen, int maxlen, boolean verplicht) {
			this.kolom = kolom;
			this.type = type;
			this.minlen = minlen;
			this.maxlen = maxlen;
			this.verplicht = verplicht;
			this.formaat = null;
		}

		parseTable(kolom kolom, String type, int minlen, int maxlen, boolean verplicht) {
			storevalues(kolom, type, minlen, maxlen, verplicht);
			this.formaat = null;
		}

		parseTable(kolom kolom, String type, int minlen, int maxlen, boolean verplicht, String formaat) {
			storevalues(kolom, type, minlen, maxlen, verplicht);
			this.formaat = formaat;
		}

		@SuppressWarnings("unused")
		parseTable(kolom kolom, String type, int minlen, int maxlen, boolean verplicht, String formaat,
				String allowedvalues) {
			storevalues(kolom, type, minlen, maxlen, verplicht);
			this.formaat = formaat;
		}
	}

	private parseTable[] importexcel = { new parseTable(importkolommen.rekeningDVV, "A", 0, 35, true),
			new parseTable(importkolommen.muntsoort, "A", 3, 3, true),
			new parseTable(importkolommen.renteDatum, "D", 8, 8, true, "yyyyMMdd"),
			new parseTable(importkolommen.byAfCode, "A", 1, 1, true),
			new parseTable(importkolommen.bedrag, "ND", 0, 14, true),
			new parseTable(importkolommen.tegenRekening, "A", 0, 35, false),
			new parseTable(importkolommen.naamRekeningHouder, "A", 0, 70, false),
			new parseTable(importkolommen.boekDatum, "D", 8, 8, true, "yyyyMMdd"),
			new parseTable(importkolommen.boekCode, "A", 2, 2, true),
			new parseTable(importkolommen.rfu1, "A", 0, 6, false),
			new parseTable(importkolommen.omschr1, "A", 0, 35, false),
			new parseTable(importkolommen.omschr2, "A", 0, 35, false),
			new parseTable(importkolommen.omschr3, "A", 0, 35, false),
			new parseTable(importkolommen.omschr4, "A", 0, 35, false),
			new parseTable(importkolommen.omschr5, "A", 0, 35, false),
			new parseTable(importkolommen.omschr6, "A", 0, 35, false),
			new parseTable(importkolommen.endToEndId, "A", 0, 35, false),
			new parseTable(importkolommen.idTegenRekHouder, "A", 1, 35, false),
			new parseTable(importkolommen.mandaatId, "A", 0, 35, false) };

	private String separator = ",";
	public static void main(String[] args) throws IOException, ValidationException, ParseException {
         //String fileName = "/home/arnouten/dev/swiftmt940/voorbeeld.STA";
        String fileName = "C:\\Users\\tonny.THUIS\\Documents\\transactions - aangepast.csv";

        LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName)));
        RaboCSVFile file = new RaboCVSParser(",").parse(reader);
		for (RaboCSVEntry record : file.getRecords())
	    {
            System.out.println(record.toString());
	    }
	}
	public RaboCVSParser(String separator){
		this.separator = separator;
	}
    /**
     * Parse the raboCSV-file. Each line is a record
     *
     * @param reader reader
     * @return RaboCSVFile instance
     * @throws IOException    An IO exception occurred
     * @throws ParseException 
     * @throws ValidationException 
     */
    public RaboCSVFile parse(final LineNumberReader reader) throws IOException, ValidationException, ParseException {
        final RaboCSVFile RaboCSVFile = new RaboCSVFile();

        String currentLine = reader.readLine();
        while (currentLine != null) {
            RaboCSVFile.getRecords().add(parseRecord(currentLine, separator));
            currentLine = reader.readLine();
        }
        return RaboCSVFile;
    }

	private boolean isEmtpyRow(String[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if (!tokens[i].isEmpty())
				return false;
		}
		return true;
	}
	
	private BigDecimal parseDecimal(String strdecimal) throws ValidationException {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "##0,00";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		BigDecimal bigDecimal;
		decimalFormat.setParseBigDecimal(true);

		// parse the string
		try {
			bigDecimal = (BigDecimal) decimalFormat.parse(strdecimal);
		} catch (ParseException e) {
			throw new ValidationException("Unexpected decimal parsing error");
		}
		return bigDecimal;
	}

	private Date parseDate(String strdate, String format) throws ValidationException {
		DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
		Date result;
		try {
			if (strdate.isEmpty())
				result = null;
			else
				result = df.parse(strdate);
		} catch (ParseException e) {
			throw new ValidationException("Unexpected date parsing error");
		}
		return result;
	}

	private importresult parseLine(String[] tokens, StringBuilder errormessage, parseTable[] importexcel) {

		for (int i = 0; (i < tokens.length && i < importexcel.length); i++) {

			if (importexcel[i].verplicht)
				if (tokens[i].isEmpty()) {
					errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString() + ") is niet ingevuld");
					return importresult.requiredfieldmissing;
				}
			if ((tokens[i].length() > importexcel[i].maxlen) || (tokens[i].length() < importexcel[i].minlen)) {
				if (importexcel[i].verplicht == false && tokens[i].length() == 0) {
					;
				} else {
					errormessage
							.append("Veld " + i + "(" + importexcel[i].kolom.toString() + ") heeft ongeldige lengte");
					return importresult.invalidlength;
				}
			}
			if (tokens[i].length() > 0) {
				if (importexcel[i].type == "N") {
					try {
						Integer.parseInt(tokens[i]);
					} catch (NumberFormatException e) {
						errormessage
								.append("Veld " + i + "(" + importexcel[i].kolom.toString() + ") is geen geldig getal");
						return importresult.formaterror;
					}
				} else if (importexcel[i].type == "ND") {
					try {
						parseDecimal(tokens[i]);
					} catch (ValidationException e) {
						errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString()
								+ ") is geen geldig decimaal getal(nn.n)");
						return importresult.formaterror;
					}
				} else if (importexcel[i].type == "D") {
					try {
						parseDate(tokens[i], importexcel[i].formaat);
					} catch (ValidationException e) {
						errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString()
								+ ") is geen geldige datum (" + importexcel[i].formaat + ")");
						return importresult.invaliddate;
					}
				} else if (importexcel[i].type == "A") {
					// do nothing
				} else {
					errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString()
							+ ") heeft onbekend datatype. Neem contact op met T. Gieselaar");
					return importresult.invalidtype;
				}
			}
		}
		return importresult.ok;
	}

    private RaboCSVEntry parseRecord(final String strLine, String separator) throws ValidationException, ParseException {
    	RaboCSVEntry RaboCSVEntry = new RaboCSVEntry();
		// Break comma separated line using the separator in the
		// calling parameters
		importresult result = importresult.ok;
		String[] tokens = strLine.split(separator);
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].replaceAll("^\"|\"$", "");
		}

		if (importexcel.length > tokens.length) {
			throw new ValidationException("Aantal kolommen(" + tokens.length
					+ ") niet zoals verwacht(" + importexcel.length + ")");
		}

		StringBuilder errormessage = new StringBuilder();
		if (isEmtpyRow(tokens)) {
			throw new ValidationException("Lege regel gevonden");
		} else {
			result = parseLine(tokens, errormessage, importexcel);
			if (result != importresult.ok) {
				throw new ValidationException(errormessage.toString());
			}
		}

		if (tokens[importkolommen.byAfCode.ordinal()].equalsIgnoreCase("C")) {
			RaboCSVEntry.setDebitCreditIndicator(DebitCreditIndicator.CREDIT);
		}else{
			RaboCSVEntry.setDebitCreditIndicator(DebitCreditIndicator.DEBIT);
		}
		RaboCSVEntry.setTransactionType(tokens[importkolommen.boekCode.ordinal()]);
		RaboCSVEntry.setValutaDatum(parseDate(tokens[importkolommen.boekDatum.ordinal()],"yyyyMMdd"));
		RaboCSVEntry.setAmount(parseDecimal(tokens[importkolommen.bedrag.ordinal()]));
		RaboCSVEntry.setIban(tokens[importkolommen.tegenRekening.ordinal()]);
		RaboCSVEntry.setName(tokens[importkolommen.naamRekeningHouder.ordinal()]);
		RaboCSVEntry.setOmschrijvingen(tokens[importkolommen.omschr1.ordinal()] + " " +
									   tokens[importkolommen.omschr2.ordinal()] + " " +
									   tokens[importkolommen.omschr3.ordinal()] + " ");
        return RaboCSVEntry;
    }

}
