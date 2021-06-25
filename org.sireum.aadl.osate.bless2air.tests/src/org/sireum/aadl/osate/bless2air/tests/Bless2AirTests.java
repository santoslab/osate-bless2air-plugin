package org.sireum.aadl.osate.bless2air.tests;

import java.io.File;

import org.junit.Test;
import org.sireum.aadl.osate.tests.SireumTest;

//@InjectWith(BLESSInjectorProvider.class)
public class Bless2AirTests extends SireumTest {

	{
		generateExpected = false;
		writeResults = true;

		// PreferenceValues.setPROCESS_BA_OPT(true);
	}

	static File ROOT_DIR = new File("./models/");

	@Test
	public void building_control() {
		execute(new File(ROOT_DIR, "building-control-bless-mixed"), "BuildingControl_Bless.aadl",
				"BuildingControlDemo.i");
	}
}
