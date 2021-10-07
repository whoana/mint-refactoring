package pep.per.mint.common.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
//import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pep.per.mint.common.data.basic.Requirement;

public class UtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public final void testGetFormatedDate(){
		System.out.println(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
	}

	@Test
	public void testBase64 () throws Exception {

		String data = "SELECT * FROM DUAL WHERE SEQ = 1";
		String encodeData = Util.encodeBase64URLData(data);
		//String encodeData2 = URLEncoder.encode(encodeData,"utf-8");
		String decodeData = Util.decodeBase64URLData(encodeData);
		//String decodeData2 = Util.decodeBase64URLData(URLDecoder.decode(encodeData2, "utf-8"));

		System.out.println("origin data:" + data);
		System.out.println("encode data:" + encodeData);
		System.out.println("decode data:" + decodeData);

		//System.out.println("encode data2:" + encodeData2);
		//System.out.println("decode data2:" + decodeData2);

		System.out.println(Util.decodeBase64URLData("U0VMRUNUJTIwKiUyMEZST00lMjBEVUFMJTIwV0hFUkUlMjBTRVElMjAlM0MlM0UlMjAxJTNC"));

	}


	@Test
	public final void testEncode() {
		try {
			String USNAME = "......";
			byte[] b = USNAME.getBytes("euc-kr"); //<--원래 캐릭터셋
			String CONVERT = new String(b,"UTF-8"); //<-- 타겟 캐릭터셋
			System.out.println(CONVERT);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public final void testToString() throws UnsupportedEncodingException {
		/*byte[] bytes = {'a', 'b', 'c'};
		String charsetName = "EUC-KR";
		System.out.println(Util.toString(bytes, charsetName));
		System.out.println("["+Util.leftPad("ȯ����",10," ") +"]");



		String list[] = StringUtils.split("a/b/c", "/.,:");

		for (String string : list) {
			System.out.println(string);
		}
		System.out.println("----------------------");

		 list = StringUtils.split("a.b.c", "/.,:");

		for (String string : list) {
			System.out.println(string);
		}

		System.out.println(Util.getFormatedDate("yyyyMMddHHmmss"));

		byte[] testByte = "lt-in-formats.xslt-in-formats.xslt-in-formats.xslt-in-formats.".getBytes();
		for(byte b : testByte){
			System.out.print(StringUtils.upperCase(Integer.toHexString(b)) + " ");
		}



		String dump = Util.hexdump(testByte);
		System.out.print(dump);
		*/

		/*String string = "{\"type\":1,\"value\":\"abcd\"}";
		Map<String, Object> r = Util.jsonToMap(string);
		Object type = r.get("type");
		Object value= r.get("value");
		System.out.println(type.getClass() +":"+ type);
		System.out.print(value.getClass() +":"+ value);*/



	}

	@Test
	public void testGetMD5(){
		String passwd = "iip";
		String encodingValue = Util.getMD5(passwd);
		System.out.println("passwd:" + passwd);
		System.out.println("encodingValue:"+ encodingValue);
	}

	@Test
	public final void testCompare(){

		Requirement origin = new Requirement();
		origin.setRequirementId("1234567890");
		origin.setStatus("A1");
		origin.setDevExpYmd("20150801");
		origin.setTestExpYmd("20150801");

		Requirement target = new Requirement();
		target.setRequirementId("1234567890");
		target.setStatus("B1");
		target.setDevExpYmd("20150803");
		target.setTestExpYmd("20150802");

		List<String> changedList = null;
		try {
			long elapsed = System.currentTimeMillis();
			changedList = Util.<Requirement>compare(origin, target, "requirementId", "status", "devExpYmd", "testExpYmd");
			System.out.println("elapsed:" + (System.currentTimeMillis() - elapsed));
			for (String fieldName : changedList){
				System.out.println(fieldName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetLastDayOfMonth(){
		int lastDay = Util.getLastDayOfMonth(2015, 5);
		System.out.println("last day:" + lastDay);
		System.out.println("m:" + Util.getFormatedDate("MM"));

		System.out.println("201510".substring(0,4) + ", " +"201510".substring(4,6));
	}




	@Test
	public void testMain(){
		float rate = 0.08f;
		int amt = 10000000;
		int dur = 10;
		long orgAmt = amt;
		long lastAmt = 0;
		for(int i = 0 ; i < dur ; i ++){
			lastAmt = Math.round(orgAmt * (1 + rate));

			System.out.println((i + 1) + "년, 원금:" + orgAmt + ", 복리이자포함:" + lastAmt);
			orgAmt = lastAmt;
		}
	}


	@Test
	public void testConvert(){
		String msg = "가나다라마바사아자차카타파하1234567890abcdefghijklmnopqrstuvwxyz";
		try {
			FileOutputStream fos = new FileOutputStream(new File("./whoana.txt"));
			byte []b = msg.getBytes("euc-kr");
			fos.write(b);

			fos.flush();
			fos.close();

			System.out.println("b:"+ new String(b,"euc-kr"));
			System.out.println("b:"+ new String(b));

			System.out.println("convert:"+ Util.convert(msg, "euc-kr"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Test
	public void testGetTimeStamp(){
		String msg = "20181105101112444";
		try {

			long tim1 = Util.getTimestamp(msg,"yyyyMMddhhmmssSSS" );
			System.out.println("b:"+tim1);
		} catch (Exception e) {
		}
		{
			String msg1 = "20181105101112445";
			try {

				long tim2 = Util.getTimestamp(msg1,"yyyyMMddhhmmssSSS" );
				System.out.println("b:"+tim2);
			} catch (Exception e) {
			}
		}

	}

	@Test
	public void test_pure_java_generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidAlgorithmParameterException {
		// Given
		final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
		keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1")); // == P256

		// When
		final KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// Then
		// Nothing Happen
		System.out.println("ecKey.publicKey: {}: "+ Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
		System.out.println("ecKey.privateKey: {} : "+  Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
	}

}
