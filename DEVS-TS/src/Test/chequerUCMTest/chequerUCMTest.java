package Test.chequerUCMTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import chequerUCM.Chequer;

public class chequerUCMTest {

	@Test
	public void testIsValid() {
		Chequer chequer1 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba1.jucm");
		Chequer chequer2 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba2.jucm");
		Chequer chequer3 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba3.jucm");
		Chequer chequer4 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba4.jucm");
		Chequer chequer5 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba5.jucm");
		Chequer chequer6 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba6.jucm");
		Chequer chequer7 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba7.jucm");
		Chequer chequer8 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba8.jucm");
		Chequer chequer9 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba9.jucm");
		Chequer chequer10 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba10.jucm");
		Chequer chequer11 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba11.jucm");
		Chequer chequer12 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba12.jucm");
		Chequer chequer13 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba13.jucm");
		Chequer chequer14 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba14.jucm");
		Chequer chequer15 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba15.jucm");
		Chequer chequer16 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba16.jucm");
		Chequer chequer17 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba17.jucm");
		Chequer chequer18 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba18.jucm");
		Chequer chequer19 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba19.jucm");
		Chequer chequer20 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba20.jucm");
		Chequer chequer21 = new Chequer(System.getProperty("user.dir") + "/src/Test/chequerUCMTest/UCM/prueba21.jucm");

		assertEquals("there is more than a end point \n", chequer1.isValid());
		assertEquals(
				"there is more than a start point \nthe parameter of the start point were not entered correctly \n",
				chequer2.isValid());
		assertEquals("responsibilities parameters were not entered correctly \n", chequer3.isValid());
		assertEquals("responsibilities parameters were not entered correctly \n", chequer4.isValid());
		assertEquals("responsibilities parameters were not entered correctly \n", chequer5.isValid());
		assertEquals("responsibilities parameters were not entered correctly \n", chequer6.isValid());
		assertEquals("responsibilities parameters were not entered correctly \n", chequer7.isValid());
		assertEquals("", chequer8.isValid());
		assertEquals("there are path elements are in the definition but not in the picture \n", chequer9.isValid());
		assertEquals("there are path elements are in the definition but not in the picture \n", chequer10.isValid());
		assertEquals("there are duplicate names path elements \n", chequer11.isValid());
		assertEquals("there are duplicate names path elements \n", chequer12.isValid());
		assertEquals("there are no responsabilities \n", chequer13.isValid());
		assertEquals("there are no components \n", chequer14.isValid());
		assertEquals("", chequer15.isValid());
		assertEquals("the parameter of the start point were not entered correctly \n", chequer16.isValid());
		assertEquals("the parameter of the start point were not entered correctly \n", chequer17.isValid());
		assertEquals("", chequer18.isValid());
		assertEquals("the parameter of the or fork were not entered correctly \n", chequer19.isValid());
		assertEquals("the parameter of the or fork were not entered correctly \n", chequer20.isValid());
		assertEquals("the parameter of the or fork were not entered correctly \n", chequer21.isValid());
	}

}
