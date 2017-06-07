package com.gieselaar.verzuimbeheer.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.utils.VerzuimProperties.__verzuimproperty;

public class TestVerzuimProperties {

	@Test
	public void testLoadProperties() {
		VerzuimProperties props = new VerzuimProperties();
		props.loadProperties();
		System.out.println(__verzuimproperty.Hostname + ":" + props.getProperty(__verzuimproperty.Hostname));
		System.out.println(__verzuimproperty.lastdocsavedir + ":" + props.getProperty(__verzuimproperty.lastdocsavedir ));
		System.out.println(__verzuimproperty.lastexportdir + ":" + props.getProperty(__verzuimproperty.lastexportdir ));
		System.out.println(__verzuimproperty.Portnumber + ":" + props.getProperty(__verzuimproperty.Portnumber ));
		System.out.println(__verzuimproperty.username + ":" + props.getProperty(__verzuimproperty.username ));
		System.out.println(props.getPropertyFilename());
		props.saveProperties();
		Object savedprop = props.getProperty(__verzuimproperty.lastdocsavedir);
		props.setProperty(__verzuimproperty.lastdocsavedir, "Test");
		props.saveProperties();
		System.out.println(__verzuimproperty.lastdocsavedir + ":" + props.getProperty(__verzuimproperty.lastdocsavedir ));
		props.setProperty(__verzuimproperty.lastdocsavedir, savedprop);
		props.saveProperties();
		assertEquals(savedprop, props.getProperty(__verzuimproperty.lastdocsavedir));
	}

}
