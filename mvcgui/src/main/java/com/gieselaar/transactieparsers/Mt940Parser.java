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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.gieselaar.transactieparsers.Mt940Entry.DebitCreditIndicator;
	
	/**
	 * @author Arnout Engelen
	 * @author Miroslav Holubec
	 */
	public class Mt940Parser {

	    /**
	     * Invoke the Mt940-parser stand alone: for testing.
	     *
	     * @param args
	     * @throws IOException
	     * @throws ParseException
	     */
	    public static void main(String[] args) throws IOException, ParseException {
	         String fileName = "C:\\Users\\tonny.THUIS\\Documents\\MT940160618164016 heel2015 tenm 1-3-2016.STA";

	         LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName)));
	         Mt940File file = new Mt940Parser().parse(reader);
	         for (Mt940Record record : file.getRecords())
	         {
	             for (Mt940Entry entry : record.getEntries())
	             {
	                 System.out.println(entry.toString());
	             }
	         }
	     }

	    /**
	     * Parse the Mt940-file. Mt940 records are delimited by '-'.
	     *
	     * @param reader reader
	     * @return Mt940File instance
	     * @throws IOException    An IO exception occurred
	     * @throws ParseException 
	     */
	    public Mt940File parse(final LineNumberReader reader) throws IOException, ParseException {
	        final Mt940File mt940File = new Mt940File();

	        List<String> recordLines = new ArrayList<>();

	        String currentLine = reader.readLine();
	        while (currentLine != null) {
	            if (currentLine.startsWith("-")) {
	                // Parse this record and add it to the file
	                mt940File.getRecords().add(parseRecord(recordLines));

	                // Clear recordLines to start on the next record
	                recordLines = new ArrayList<>();
	            } else {
	                recordLines.add(currentLine);
	            }
	            currentLine = reader.readLine();
	        }

	        // A file might not end with a trailing '-' (e.g. from Rabobank):
	        mt940File.getRecords().add(parseRecord(recordLines));

	        return mt940File;
	    }

	    /**
	     * An mt940-record first has a couple of 'header' lines that do not
	     * start with a ':'.
	     * <p>
	     * After that, a line that doesn't start with a ':' is assumed to
	     * belong to the previous 'real' line.
	     *
	     * @param recordLines list of records
	     * @return List of strings that have been correctly merged
	     */
	    private static List<String> mergeLines(final List<String> recordLines) {
	        List<String> retVal = new ArrayList<>();
	        String currentString = null;
	        boolean inMessage = false;
	        for (String string : recordLines) {
	            // Are we in the message proper, after the
	            // header?
	            if (inMessage) {
	                if (string.startsWith(":")) {
	                    retVal.add(currentString);
	                    currentString = "";
	                }
	                currentString += string + " ";
	            } else {
	                if (string.startsWith(":")) {
	                    // then we're past the header
	                    inMessage = true;
	                    currentString = string;
	                } else {
	                    // add a line of the header
	                    retVal.add(string);
	                }
	            }
	        }
	        return retVal;
	    }

	    /**
	     * An mt940-record consists of some general lines and a couple
	     * of entries consisting of a :61: and a :86:-section.
	     *
	     * @param recordLines the List of MT940 records to parse
	     * @return and generate Mt940 Record
	     * @throws ParseException 
	     */
	    private static Mt940Record parseRecord(final List<String> recordLines) throws ParseException {
	        Mt940Record mt940Record = new Mt940Record();

	        // Merge 'lines' that span multiple actual lines.
	        List<String> mergedLines = mergeLines(recordLines);

	        Mt940Entry currentEntry = null;
	        for (String line : mergedLines) {
	            if (line.startsWith(":61:")) {
	                currentEntry = nextEntry(mt940Record.getEntries(), currentEntry);

	                line = line.substring(4);
	                line = parseDatumJJMMDD(currentEntry, line);
	                // for now don't handle the Entrydate. It is optional.
	                if (startsWithEntryDate(line)) {
	                    line = line.substring(4);
	                }
	                // for now only support C and D, not RC and RD
	                line = parseDebitCreditIndicator(currentEntry, line);
	                line = parseAmount(currentEntry, line);
	                line = parseTransactionType(currentEntry, line);
	            }
	            if (line.startsWith(":86:") && currentEntry != null) {
	                currentEntry.addToOmschrijvingen(line.substring(4));
	                line = parseIban(currentEntry, line);
	                line = parseBic(currentEntry, line);
	                line = parseNaam(currentEntry, line);
	            }
	        }

	        // add the last one:
	        nextEntry(mt940Record.getEntries(), currentEntry);

	        return mt940Record;
	    }

	    private static String parseNaam(Mt940Entry currentEntry, String line) {
			/**
			 * In the statements we receive we try to find the name of accountholder. 
			 */
			int startpos = line.indexOf("NAAM:");
			if (startpos < 0){
				return line;
			}else{
				/**
				 * find the end of next keyword (ends with ':')
				 * then search backwards for the first space to find the start
				 * of the next keyword
				 */
				int endpos = line.indexOf(":", startpos+5);
				if (endpos < 0){
					endpos = line.length()-1;
				}
				while ((!line.substring(endpos, endpos + 1).equals(" ")) && (endpos > startpos)) endpos--;
				if (endpos > startpos){
					/**
					 * found start of next keyword which is the end of the name field
					 */
					currentEntry.setName(line.substring(startpos+5, endpos).trim());
					return line.substring(endpos);
				}else{
					return line;
				}	
			}
		}

		private static String parseBic(Mt940Entry currentEntry, String line) {
			/**
			 * In the statements we receive we try to find the BIC. 
			 */
			int startpos = line.indexOf("BIC:");
			if (startpos < 0){
				return line;
			}else{
				int endpos = line.indexOf(" ", startpos+5);
				if (endpos < 0){
					return line;
				}
				currentEntry.setBic(line.substring(startpos+4, endpos).trim());
				return line.substring(endpos);
			}
		}

		private static String parseIban(Mt940Entry currentEntry, String line) {
			/**
			 * In the statements we receive we try to find the IBAN. The IBAN itself is
			 * followed by a space.
			 */
			int startpos = line.indexOf("IBAN:");
			if (startpos < 0){
				return line;
			}else{
				int endpos = line.indexOf(" ", startpos + 6);
				if (endpos < 0){
					return line;
				}
				currentEntry.setIban(line.substring(startpos+5, endpos).trim());
				return line.substring(endpos);
			}
		}

		private static String parseTransactionType(Mt940Entry currentEntry, String line) {

	        if (line.startsWith("N") || line.startsWith("F")){
	
		        String type = line.substring(0, 4);
		        currentEntry.setTransactionType(type);
	
		        return line.substring(3);
	        }else{
	        	return line;
	        }
		}

		/**
	     * adds the current entry to the result as a side-effect, if available
	     *
	     * @param entries       entry list
	     * @param previousEntry entry to add if not null;
	     * @return new working {@code Mt940Entry}
	     */
	    private static Mt940Entry nextEntry(List<Mt940Entry> entries, Mt940Entry previousEntry) {
	        if (previousEntry != null) {
	            entries.add(previousEntry);
	        }
	        return new Mt940Entry();
	    }

	    /**
	     * EntryDate is a 4-character optional field - but how can we check whether it was included?
	     * <p>
	     * The field is directly followed by the mandatory 'D/C' character, so
	     * we assume that when the string starts with a digit that's probably the entrydate
	     *
	     * @param line line to check for Entrydate
	     * @return true if found
	     */
	    private static boolean startsWithEntryDate(final String line) {
	        return line != null && line.matches("^\\d.*");
	    }

	    /**
	     * Parse the value, put it into the entry.
	     *
	     * @param currentEntry working {@code Mt940Entry}
	     * @param line line to parse decimal value from
	     * @return the rest of the string to be parsed
	     */
	    private static String parseAmount(final Mt940Entry currentEntry, final String line) {
	        int endIndex = line.indexOf('N');
	        if (endIndex < 0) {
	            endIndex = line.indexOf('F');
	        }

	        String decimal = line.substring(0, endIndex);
	        decimal = decimal.replaceAll(",", ".");
	        currentEntry.setAmount(new BigDecimal(decimal).setScale(2));

	        return line.substring(endIndex);
	    }

	    /**
	     * Parse the debit/credit value, put it into the entry.
	     *
	     * @param currentEntry working {@code Mt940Entry}
	     * @param string credit / debit line to parse
	     * @return the rest of the string to be parsed
	     */
	    private static String parseDebitCreditIndicator(final Mt940Entry currentEntry, final String string) {
	        String s = string;

	        if (string.startsWith("D")) {
	            currentEntry.setDebitCreditIndicator(DebitCreditIndicator.DEBIT);
	            s = string.substring(1);
	        } else if (string.startsWith("C")) {
	            currentEntry.setDebitCreditIndicator(DebitCreditIndicator.CREDIT);
	            s = string.substring(1);
	        } else {
	            throw new UnsupportedOperationException("DebitCreditIndicator " + s + " not yet supported");
	        }
	        return s;
	    }

	    /**
	     * Parse the formatted date, put it into the entry.
	     *
	     * @param currentEntry working {@code Mt940Entry}
	     * @param string string to parse date from
	     * @return the rest of the string to be parsed
	     * @throws ParseException 
	     */
	    private static String parseDatumJJMMDD(final Mt940Entry currentEntry, final String string) throws ParseException {
	        final DateFormat dateTimeFormatter = new SimpleDateFormat("yyMMdd", Locale.getDefault());

	        final String date = string.substring(0, 6);

	        currentEntry.setValutaDatum(dateTimeFormatter.parse(date));

	        return string.substring(6);
	    }
	}
