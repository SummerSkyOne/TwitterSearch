package org.tartarus.snowball;

public class Test {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		 Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
		 SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
		  stemmer.setCurrent("Amazing");
		  stemmer.stem();
		  System.out.println(stemmer.getCurrent());  
	}

}
