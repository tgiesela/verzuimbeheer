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
import java.math.BigDecimal;
import java.util.Date;

public class Mt940Entry {
    // 'Credit', in mt940, means money was transferred 
    // to the current account.
    public enum DebitCreditIndicator {
        CREDIT,
        DEBIT,
        STORNO_DEBIT,
        STORNO_CREDIT
    }

    private Date valutaDatum;

    private DebitCreditIndicator debitCreditIndicator;

    private BigDecimal amount;

    private String omschrijvingen;
    
    private String transactionType;
    
    private String iban;
    
    private String bic;
    
    private String name;

    public Date getValutaDatum() {
        return valutaDatum;
    }

    public void setValutaDatum(final Date valutaDatum) {
        this.valutaDatum = valutaDatum;
    }

    @Override
    public String toString() {
        return "At " + valutaDatum + ", " + debitCreditIndicator + ": " + amount + ", Type: " + transactionType + " for " + omschrijvingen;
    }

    public DebitCreditIndicator getDebitCreditIndicator() {
        return debitCreditIndicator;
    }

    public void setDebitCreditIndicator(DebitCreditIndicator debitCreditIndicator) {
        this.debitCreditIndicator = debitCreditIndicator;
    }

    public String getOmschrijvingen() {
        return omschrijvingen;
    }

    public void setOmschrijvingen(String omschrijvingen) {
        this.omschrijvingen = omschrijvingen;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

	public void addToOmschrijvingen(String string) {
		if (omschrijvingen == null || "".equals(omschrijvingen.trim()))
		{
			omschrijvingen = string;
		}
		else
		{
			omschrijvingen += " ";
			omschrijvingen += string;
		}
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
