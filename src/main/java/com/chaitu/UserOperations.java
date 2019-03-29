package com.chaitu;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.helpers.FileUtils;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import lombok.Getter;
import lombok.Setter;

public class UserOperations {
	private String loginUrl;
	
	
	private String scriptUrl;
	
	private static final String METHOD_LOGIN="login";
	private static final String METHOD_GET_USER_ID="getUserId";
	private static final String METHOD_CHECK_SESSION="checkSession";
	private static final String METHOD_GET_ALL_SITES_FOR_USER="getAllSitesForUser";
	private static final String METHOD_GET_PAGES_AND_TOOLS_FOR_SITE="getPagesAndToolsForSite";
	
	
	
	
	public String getLoginUrl() {
		return "http://localhost:8080/sakai-ws/soap/portallogin?wsdl";
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getScriptUrl() {
		return "http://localhost:8080/sakai-ws/soap/sakai?wsdl";
	}

	public void setScriptUrl(String scriptUrl) {
		this.scriptUrl = scriptUrl;
	}

	public static void main(String[] args) throws IOException {
		UserOperations operations = new UserOperations();
		String session = operations.getNewAdminSession();
		System.out.println("session:"+session);
		
		
		String userId="wanna.admin";
		String hasRole = operations.hasAdminRole(session, userId) ;
		System.out.println("result:"+hasRole);
		
		
		
		String sites = operations.getSitesByTitle(session, "JAVA CORE 1 FALL 2009");
		System.out.println("sites:"+sites);
		String result = operations.assignAdminRole(session, userId);
		System.out.println("result:"+result);
		
		
		
		
		/*String grades = operations.getGrades(session);
		System.out.println("sites:"+grades);*/
		//String user= operations.addNewUserWithImage(session);
		//System.out.println("user"+user);
	}
	public String getGradebookUrl() {
		return "http://localhost:8080/sakai-ws/soap/gradebook?wsdl";
	}
	
	public String addNewUserWithImage(String session) throws IOException{
		File file = new File("C:\\Users\\VIDYAYUG\\Pictures\\code.png");
		byte[] encoded = java.util.Base64.getEncoder().encode(org.apache.commons.io.FileUtils.readFileToByteArray(file));
		String image =new String(encoded, StandardCharsets.US_ASCII);
			String imageMimeType ="image/png";
		//setup data to send
				Object[] data= new Object[9];
				data[0] = (Object) session;
				data[1] = (Object) "XYZABC";
				data[2] = (Object) "XYZABC";
				data[3] = (Object) "XYZABC";
				data[4] = (Object) "XYZABC@gmail.com";
				data[5] = (Object) "registered";
				data[6] = (Object) "XYZABC";
				data[7] = (Object) image;
				data[8] = (Object) imageMimeType;
				String user = call(getScriptUrl(), "addNewUserWithImage", data);
				return user;
	}
	private  String assignAdminRole(String session, String userId){
		Object[] data= new Object[4];
		data[0] = (Object) session;
		data[1] = (Object) "!admin";
		data[2] = (Object) userId;
		data[3] = (Object) "admin";
		String result = call(getScriptUrl(), "addMemberToSiteWithRole", data);
		return result;
	}
	private  String hasAdminRole(String session, String userId){
		Object[] data= new Object[3];
		data[0] = (Object) session;
		data[1] = (Object) "!admin";
		data[2] = (Object) userId;
		String result = call(getScriptUrl(), "checkForMemberInSite", data);
		return result;
	}
	
	private  String getGrades(String session) {
		
		//setup data to send
		Object[] data= new Object[1];
		data[0] = (Object) session;
		
		String sites = call(getScriptUrl(), "getUserGrades", data);
		
		
		//and return it
		return sites;
	}
	
	/**
	 * Make a web service call to the given endpoint, calling the method and using the params supplied
	 * @param endpoint	wsdl url
	 * @param method	method to call
	 * @param params	Array of params:
	 *  1. Must be in order required to be sent
	 * 	 * @return the response, or null if any exception is thrown.
	 */
	public static String call(String endpoint, String method, Object[] params) {
		
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		
		try {
			Client client = dcf.createClient(endpoint);
			Object[]  returnVals = client.invoke(method, params);
			//extract returned value. getting 1st value as sakai ws calls returns only one value.
			if(returnVals!=null && returnVals.length>0 ) {
				if(returnVals[0] instanceof String) {
					return (String)returnVals[0];
				}else if(returnVals[0] instanceof Boolean) {
					System.out.println(returnVals[0]);
					return Boolean.toString((Boolean)returnVals[0]);
				}
			}
				
		}
		catch (Exception e) {
			e.printStackTrace();
			//log.error("A connection error occurred: " + e.getClass() + ": " + e.getMessage());
		}
		return null;
	}
	/**
	 * Get a new session for the admin user. Don't call this directly, use getSession() instead.
	 * @return
	 */
	private  String getNewAdminSession() {
		String session = null;
		//setup data to send
		Object[] data= new Object[2];
		data[0] = (Object) getAdminUsername();
		data[1] = (Object)getAdminPassword();
		
		session = call(getLoginUrl(), METHOD_LOGIN, data);
		
		
		//and return it
		return session;
	}
	
	private  String getSitesByTitle(String session , String title) {
		
		//setup data to send
		Object[] data= new Object[2];
		data[0] = (Object) session;
		data[1] = (Object)title;
		
		String sites = call(getScriptUrl(), "findSitesByTitle", data);
		
		
		//and return it
		return sites;
	}

	private Object getAdminPassword() {
		// TODO Auto-generated method stub
		return "plugh";
	}

	private Object getAdminUsername() {
		// TODO Auto-generated method stub
		return "admin";
	}
	
	
	
}
