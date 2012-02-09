package com.seb.chess.core;



public class ChessCoreLog {

	public static void logDebug(String logMessage){
		System.out.println(logMessage);
		//ChessCore.getDefault().getLog().log(new Status(Status.OK,ChessCore.PLUGIN_ID,Status.OK,logMessage,null));
	}
	
}
