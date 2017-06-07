package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;

public class TerugdraaienMaandDialog extends BaseDetailform{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoginSessionRemote loginSession;
	private DatePicker dtpMaand;
	private Component thisform = this;

	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}

	public void setInfo(InfoBase info) {
		Date vandaag = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(vandaag);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -1);
		cal.get(Calendar.YEAR);
		try {
			dtpMaand.setDate(cal.getTime());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		}
	}

	public TerugdraaienMaandDialog(JFrame frame, boolean modal) {
		super("Terugdraaien maand");
		initialize();
		this.getOkButton().setEnabled(false);

	}

	private void initialize() {
		setBounds(100, 100, 212, 151);

		dtpMaand = new DatePicker();
		dtpMaand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dtpMaand.getDate());

					Integer aantal = ServiceCaller.factuurFacade(loginSession)
							.getAantalontbrekendeFacturen(
									cal.get(Calendar.YEAR),
									cal.get(Calendar.MONTH)+1);
					//if (aantal == 0) {
						((BaseDetailform) thisform).getOkButton().setEnabled(true);
					//} else {
					//	((BaseDetailform) thisform).getOkButton().setEnabled(false);
					//}
				} catch (ServiceLocatorException | PermissionException
						| ValidationException | VerzuimApplicationException ee) {
					ExceptionLogger.ProcessException(ee, thisform, false);
					return;
				}
			}
		});
		dtpMaand.setBounds(30, 34, 120, 23);
		dtpMaand.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpMaand);
	}

	protected void okButtonClicked(ActionEvent e) {
		int jaar, maand;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtpMaand.getDate());

		jaar = cal.get(Calendar.YEAR);
		maand = cal.get(Calendar.MONTH)+1;

		if (this.loginSession != null) {
			try {
				ServiceCaller.factuurFacade(loginSession).terugdraaienMaand(jaar,
						maand);
				super.okButtonClicked(e);
			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1, this);
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, this);
			} catch (Exception e1) {
				ExceptionLogger.ProcessException(e1, this);
			}
		} else
			JOptionPane.showMessageDialog(this,
					"Logic error: loginSession is null");
	}

}
