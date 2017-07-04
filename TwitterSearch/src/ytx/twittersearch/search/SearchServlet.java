package ytx.twittersearch.search;


import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.*;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.SnowballStemmer;

import java.util.Map.Entry;
import java.util.regex.*;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static ArrayList<TweetRecord> tweetArray=new ArrayList<TweetRecord>();
	static ArrayList<String> relatedpic=new ArrayList<String>();
	static ArrayList<String> querys=new ArrayList<String>();
	boolean tohb=true;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		tweetArray.clear();
		relatedpic.clear();
		querys.clear();
		StringBuilder res = new StringBuilder();
		String method=request.getParameter("method");
		
		String query=request.getParameter("query");
		int page=1;
		if(request.getParameter("page")!=null)
			page=Integer.parseInt(request.getParameter("page"));
		
		//String method=request.getParameter("method");
		String luceneChecked="";
		String hadoopChecked="";
		String tweet="";
		String hashtag="";
		String toh=request.getParameter("tweetOrHashtag");
		String tohCheck="hashtag";
		
		if(toh==null)
			toh="tweet";
		if(toh.equals(tohCheck)){
			tweet="";
			hashtag="active";
			tohb=false;
		}else{
			tweet="active";
			hashtag="";
			tohb=true;
		}
		String check="lucene";
		if(method.equals(check)){
			luceneChecked="checked";
		}else{
			hadoopChecked="checked";
		}
		if(tweet=="active"&&luceneChecked=="checked"){//lucene tweet
			// lucene
			lucene(query,tohb);
			
		}else if(hashtag=="active"&&luceneChecked=="checked"){//lucene hashtag
			// lucene
			lucene(query,tohb);
		}else if(tweet=="active"&&hadoopChecked=="checked"){//hadoop tweet
			
			try {
				hadoop(query);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{//hadoop hashtag
			try {
				ArrayList<String> tul = hashtag(query);
				
				for (String entry : tul)
		        {
					StringTokenizer st=new StringTokenizer(entry);
					String user=st.nextToken();
					String id=st.nextToken();
					double score25=0;
					TweetRecord temp=getTweetRecord(id,user,query);
					if(tweetArray.size()<200)
						tweetArray.add(temp);
					else
						break;		
		        }
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		
		// hadoop(BM25)
		try {
			hadoop(query);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//BM25/lucene
		//get array

		//test
		
		//PrintWriter out = response.getWriter();
		//out.println(re);
		request.setAttribute("q", query);
		request.setAttribute("toh", toh);
		request.setAttribute("tweet", tweet);
		request.setAttribute("hashtag", hashtag);
		request.setAttribute("tweetlist", tweetArray);
		request.setAttribute("relatedpic", relatedpic);
		request.setAttribute("page", page);
		request.setAttribute("luceneChecked", luceneChecked);
		request.setAttribute("hadoopChecked", hadoopChecked);
		//request.setAttribute("tweets", tweets);
		request.getRequestDispatcher("query.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public TweetRecord getTweetRecord(String id,String name, String query) throws IOException, ParseException{
		
		
		FileReader fr = new FileReader(new File("E:/User_new/"+name+".json"));
		TweetRecord temp=new TweetRecord();
		
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		JSONParser parser = new JSONParser();
		JSONObject user = (JSONObject) parser.parse(line);
		
		//get user
		String img = (String) user.get("profileImageURL");
		temp.setProfileImageURL(img);
		
		String nickname = (String) user.get("name");
		temp.setName(nickname);
		
		temp.setScreenName(name);
		
		//get tweet
		Object list = parser.parse(new FileReader("C:\\Users\\Summer\\workspace\\twitter4j\\TitterDownload\\"+name+".json"));
		JSONArray tws=(JSONArray) list;
		for(int i=0;i<tws.size();i++){
			JSONObject obj=(JSONObject)tws.get(i);
			//creatat;text;favorite;retweet;media
			if((long)obj.get("id")!=Long.parseLong(id))
				continue;
			else{
				
				String[] stopwords = { "a", "about", "above", "above", "across", "after", "afterwards", "again", "against",
						"all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst",
						"amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway",
						"anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes",
						"becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between",
						"beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could",
						"couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg",
						"eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every",
						"everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire",
						"first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full",
						"further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here",
						"hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how",
						"however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its",
						"itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me",
						"meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",
						"my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody",
						"none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one",
						"only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own",
						"part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming",
						"seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty",
						"so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such",
						"system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence",
						"there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv",
						"thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
						"together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up",
						"upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence",
						"whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether",
						"which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with",
						"within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves" };
				//
				/*
				StringTokenizer itr = new StringTokenizer(query);
				ArrayList<String> sl = new ArrayList<String>();
				Map m = new HashMap();
				while (itr.hasMoreTokens()) {

					String s = itr.nextToken();
					s = s.replaceAll("[.,;:!?'\"-]", "");

					if (s.matches("[0-9a-zA-Z]+") && !(Arrays.asList(stopwords).contains(s.toLowerCase()))) {
						s = s.toLowerCase();
						Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
						SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
						stemmer.setCurrent(s);
						stemmer.stem();
						s = stemmer.getCurrent();
						if (!(Arrays.asList(stopwords).contains(s))) {
							sl.add(s);

							
						}
					}
				}
				
				*/
				
				//
				
				query=query.toLowerCase();
				StringTokenizer itr = new StringTokenizer(query);
				Map m = new HashMap();
				while (itr.hasMoreTokens()) {
					String s = itr.nextToken();
					System.out.println(s);
					s = s.replaceAll("[.,;:!?'\"-]", "");
					System.out.println(s);

					if (s.matches("[0-9a-zA-Z]+") && !(Arrays.asList(stopwords).contains(s.toLowerCase()))) {
						System.out.println("enter if");

						if(querys.contains(s))
							continue;
						else
							querys.add(s);
						
					}
				}
				
				String date=(String)obj.get("createdAt");
				temp.setCreatT(date);
				String text=(String)obj.get("text");
				for(int iq=0;iq<querys.size();iq++){
					if(tohb){
						text=text.replace("\0"+querys.get(iq), "\0<b>"+querys.get(iq)+"</b>");
						
						//Pattern pattern =Pattern.compile("charset=(.+?)\"");
						
						//Matcher matcher=pattern.matcher(text);
					}
					else
						text=text.replace("\0#"+querys.get(iq), "\0<b>#"+querys.get(iq)+"</b>");
				}
				temp.setText(text);
				
				if(text.length()>80){
					//
					/*
					int count=0;
					int[] intArray = {-1,-1,-1,-1};
					String tempsnip="";
					for(int iq=0;iq<querys.size();iq++){
						if(count>3)
							break;
						if(tohb)
							intArray[count++] = text.indexOf("\0"+querys.get(iq));
						else
							intArray[count++] = text.indexOf("#"+querys.get(iq));
						
					}
					System.out.print(intArray[0]);
					System.out.print(intArray[1]);
					System.out.print(intArray[2]);
					System.out.print(intArray[3]);
					
					Arrays.sort(intArray);
					String ttt="";
					StringBuffer ttttt=new StringBuffer(text);
					for(int ll=0;ll<text.length();ll++){
						if(intArray[0]!=-1 && ll>(intArray[0]-20) && ll<(intArray[0]+20))
							ttttt.setCharAt(ll, '1');
						else if(intArray[1]!=-1 && ll>(intArray[1]-20) && ll<(intArray[1]+20)){
							ttttt.setCharAt(ll, '1');
						}else if(intArray[2]!=-1 && ll>(intArray[2]-20) && ll<(intArray[2]+20)){
							ttttt.setCharAt(ll, '1');
						}else if(intArray[3]!=-1 && ll>(intArray[3]-20) && ll<(intArray[3]+20)){
							ttttt.setCharAt(ll, '1');
						}else{
							ttttt.setCharAt(ll, '0');
						}
					}
					System.out.println(ttttt);
					for(int ii=0;ii<ttttt.length();ii++){
						if(ttttt.charAt(ii)=='1'){
							if(ii>0&&ttttt.charAt(ii-1)=='0'&&ii>0){
								ttt+="...";
								
							}
							ttt+=text.charAt(ii);
						}
					}
					temp.setSnippet(ttt);
					*/
					temp.setSnippet(text.substring(0, 80));
				}else{
					temp.setSnippet(text);
				}

				//System.out.println("here 1");
				long favorite=(long)obj.get("favoriteCount");
				temp.setFavorite(favorite);
				//System.out.println("here 2");
				long retweet=(long)obj.get("retweetCount");
				temp.setRetweet(retweet);
				JSONArray urllist=(JSONArray)obj.get("mediaEntities");
				if(urllist.size()>0){
					JSONObject u=(JSONObject)urllist.get(0);
					String url=(String) u.get("mediaURL");
					temp.setMediaURL(url);
					if(relatedpic.size()<10){
						char delete='\\';
						url=url.replace(""+delete, "");
						relatedpic.add(url);
					}
				}
				//System.out.println("here 3");
				
			}
		}
		
		
		return temp;
		
	}

	public void hadoop(String query) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, ParseException {
		String[] stopwords = { "a", "about", "above", "above", "across", "after", "afterwards", "again", "against",
				"all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst",
				"amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway",
				"anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes",
				"becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between",
				"beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could",
				"couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg",
				"eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every",
				"everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire",
				"first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full",
				"further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here",
				"hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how",
				"however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its",
				"itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me",
				"meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",
				"my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody",
				"none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one",
				"only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own",
				"part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming",
				"seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty",
				"so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such",
				"system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence",
				"there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv",
				"thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
				"together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up",
				"upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence",
				"whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether",
				"which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with",
				"within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves" };
		
		StringTokenizer itr = new StringTokenizer(query);
		ArrayList<String> sl = new ArrayList<String>();
		Map m = new HashMap();
		while (itr.hasMoreTokens()) {

			String s = itr.nextToken();
			s = s.replaceAll("[.,;:!?'\"-]", "");

			if (s.matches("[0-9a-zA-Z]+") && !(Arrays.asList(stopwords).contains(s.toLowerCase()))) {
				s = s.toLowerCase();
				Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
				SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
				stemmer.setCurrent(s);
				stemmer.stem();
				s = stemmer.getCurrent();
				if (!(Arrays.asList(stopwords).contains(s))) {
					sl.add(s);

					
				}
			}
		}
		m = BM25(sl);
		m=sortByComparator(m);
		for (Entry entry : (Set<Entry>)m.entrySet())
        {
			StringTokenizer st=new StringTokenizer((String) entry.getKey());
			String user=st.nextToken();
			String id=st.nextToken();
			double score25=(double) entry.getValue();
			TweetRecord temp=getTweetRecord(id,user,query);
			if(tweetArray.size()<200)
				tweetArray.add(temp);
			else
				break;	
        }
		//entry.getKey()
		
	}
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap)
    {

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1,
                    Entry<String, Double> o2)
            {
                
               
               
                    return o2.getValue().compareTo(o1.getValue());

                
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public Map BM25(ArrayList<String> sl) throws IOException, ParseException {
		int N = 10565666;
		Map m = new HashMap();
		//
		
		//
		FileReader fr = null;
		JSONParser parser = new JSONParser();
		for (String s : sl) {
			if (Character.isDigit(s.charAt(0))) {
				fr = new FileReader(new File("E:\\index\\digit\\" + s.charAt(0) + ".json"));//hadoop index number
				JSONArray ja = (JSONArray) parser.parse(fr);
				Iterator<JSONObject> iterator = ja.iterator();

				while (iterator.hasNext()) {
					JSONObject index = iterator.next();
					String w = (String) index.get("w");
					if (w.equals(s)) {

						JSONArray ja1 = (JSONArray) index.get("x");
						double df = ja1.size();

						Iterator<JSONObject> iterator1 = ja1.iterator();

						while (iterator1.hasNext()) {
							JSONObject index1 = iterator1.next();
							String u = (String) index1.get("u");
							Long i = (Long) index1.get("i");
							Long f = (Long) index1.get("f");
							double tf = f.intValue();
							Double bm25 = score(tf, N, 10, 10, 1, df, 1.2, 8, 0.75);
							if (m.containsKey(u + " " + i)) {
								Double oldbm25 = (Double) m.get(u + " " + i);
								m.put(u + " " + i, bm25 + oldbm25);
							} else {
								m.put(u + " " + i, bm25);
							}
						}
						break;

					}
				}
			} else {
				fr = new FileReader(new File("E:\\index\\" + s.charAt(0) + "/" + s.charAt(1) + ".json"));//hadoop char index
				JSONArray ja = (JSONArray) parser.parse(fr);
				Iterator<JSONObject> iterator = ja.iterator();

				while (iterator.hasNext()) {
					JSONObject index = iterator.next();
					String w = (String) index.get("w");
					if (w.equals(s)) {

						JSONArray ja1 = (JSONArray) index.get("x");
						double df = ja1.size();

						Iterator<JSONObject> iterator1 = ja1.iterator();

						while (iterator1.hasNext()) {
							JSONObject index1 = iterator1.next();
							String u = (String) index1.get("u");
							Long l = (Long) index1.get("s");
							Long i = (Long) index1.get("i");
							Long f = (Long) index1.get("f");
							double tf = f.intValue();
							double bm25 = score(tf, N, l, 6.19, 1, df, 1.2, 8, 0.75);//query frequency
							if (m.containsKey(u + " " + i)) {
								double oldbm25 = (Double) m.get(u + " " + i);
								m.put(u + " " + i, bm25 + oldbm25);
							} else {
								m.put(u + " " + i, bm25);
							}
						}
						break;

					}
				}
			}
		}
		return m;
	}


	public ArrayList<String> hashtag(String s) throws IOException, ParseException {
		FileReader fr = new FileReader(new File("e:hashtag.txt"));//hashtag
		
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		ArrayList<String> sl = new ArrayList<String>();
		while (line != null) {
			JSONParser parser = new JSONParser();
			JSONObject index = (JSONObject) parser.parse(line);
			String hashtag = (String) index.get("h");
			
			boolean flag = false;
			if (hashtag.equals(s)) {
				flag = true;
				JSONArray ja = (JSONArray) index.get("l");
				Iterator<JSONObject> hi = ja.listIterator();
				while (hi.hasNext()) {
					
					JSONObject jo = (JSONObject) hi.next();
					String user = (String) jo.get("u");
					String id = (String) jo.get("i");
					sl.add(user + " " + id);
				}
			} else {
				if (flag == true)
					break;

			}
			line=br.readLine();
		}
		return sl;
	}

	
	public double score(double tf, 
    		double numberOfDocuments, 
    		double docLength, 
    		double averageDocumentLength, 
    		double queryFrequency, 
    		double documentFrequency,
    		double k_1,
    		double k_3,
    		double b) {
    	
            double K = k_1 * ((1 - b) + ((b * docLength) / averageDocumentLength));
            double weight = ( ((k_1 + 1d) * tf) / (K + tf) );	//first part
            weight = weight * ( ((k_3 + 1) * queryFrequency) / (k_3 + queryFrequency) );	//second part
            
            // multiply the weight with idf 
            double idf = weight * Math.log((numberOfDocuments - documentFrequency + 0.5d) / (documentFrequency + 0.5d));	
            return idf;
    }
	
	public void lucene(String s, boolean toh) throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		KeywordAnalyzer kanalyzer = new KeywordAnalyzer();
		String indexLocation = "C:\\Users\\Summer\\workspace\\twitter4j\\index";//lucene index
		IndexReader reader = DirectoryReader.open(FSDirectory.open((new File(indexLocation).toPath())));
		IndexSearcher searcher = new IndexSearcher(reader);
		// TopScoreDocCollector collector = TopScoreDocCollector.create(5);

		try {
			TopScoreDocCollector collector;
			collector = TopScoreDocCollector.create(100);
			Query q;
			if(toh){
				q = new QueryParser("text", analyzer).parse(s);
				
				
				
			}else{
				q = new QueryParser("hashtag", kanalyzer).parse(s);
				
				
				
				
			}
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// 4. display results
			System.out.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				String id=d.get("id");//tweet id
				String name=d.get("filename");//screenname.json
				name=name.replace(".json", "");
				double sc=hits[i].score;//score
				TweetRecord temp=getTweetRecord(id,name,s);
				if(tweetArray.size()<200)
					tweetArray.add(temp);
				else
					break;
				//save in list
			}
			//pass to jsp
		} catch (Exception e) {
			System.out.println("Error searching " + s + " : " + e.getMessage());
		}
	}
}
