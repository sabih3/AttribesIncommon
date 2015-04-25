package com.attribes.incommon.api;

public class BALFactory {

	private static BALFactory singleTon = null;
	private static UserBAL usrBalSingleTon = null;
	private static InterestBAL interestBal = null;
	
	private BALFactory(){
		
	}
	
	public static BALFactory getBALFactory(){
		synchronized (BALFactory.class) {
			if ( singleTon == null ) {
				synchronized (BALFactory.class) {
					singleTon = new BALFactory();
				}
			}
		}
		return singleTon;
	}
	
	
	public UserBAL getUserBAL(){
		if ( usrBalSingleTon == null )
			usrBalSingleTon = new UserBAL();
		return usrBalSingleTon;
	}
	public InterestBAL getInterestBal(){
		if ( interestBal == null ) {
			interestBal  = new InterestBAL();
		}
		return interestBal;
	}


	
	
	
}
