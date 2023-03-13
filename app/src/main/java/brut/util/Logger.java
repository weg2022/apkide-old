package brut.util;

public abstract class Logger {
	
	private static Logger logger;
	public static Logger get(){
		synchronized (Logger.class) {
			return logger;
		}
	}
	
	public static void set(Logger logger){
		synchronized (Logger.class){
			Logger.logger = logger;
		}
	}
	
	public abstract void info(String msg);
	
	public abstract void warning(String msg);
	
	public abstract void error(String msg);
}
