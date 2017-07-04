<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="java.io.*, java.util.ArrayList,java.util.Iterator,
	org.json.simple.JSONObject, org.json.simple.JSONArray,
	org.json.simple.parser.ParseException,org.json.simple.parser.JSONParser,ytx.twittersearch.search.*" %>
<%! 
	ArrayList<File> queue=new ArrayList<File>();
	String fileName="./p.txt";
	JSONParser parser = new JSONParser();
	Object obj1;
	JSONArray jsonArray;
	JSONObject temp;
	String name;
	Iterator<JSONObject> iterator;
	ArrayList<TweetRecord> tweetArray = new ArrayList<TweetRecord>();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Search Page</title>
		<link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">  
  		<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
  		<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  		<style> 
			.divcss5{ border:0; width:400px; overflow:hidden} 
			.divcss5 li{list-style:none;display:inline;}
			.divcss5 img{max-width:170px;} 
		</style> 
	</head>
	<body>
		
		<div style="padding-left:20%;padding-right:30%">
			<form action="SearchServlet" method="GET" class="form-signin">
		        <h2>Search</h2>
		        <input type="text" name="query" id="inputQuery" class="form-control" placeholder=<%=request.getParameter("query") %> style="width:600px;display:inline-block;" value="<%=request.getAttribute("q") %>" required autofocus>
		        <input type="hidden" name="tweetOrHashtag" value="<%=request.getAttribute("toh") %>">
		        <button class="btn btn-lg btn-primary" style="display:inline-block;" type="submit">Search</button>
		       	<div class="checkbox">
		          <label>
		          	<input type="radio" name="method" value="lucene" <%=request.getAttribute("luceneChecked") %>> Lucene
		            <input type="radio" name="method" value="hadoop" <%=request.getAttribute("hadoopChecked") %>> Hadoop
		          </label>
		        </div>
	      	</form>
      	</div>
		
		<div class="tabbable" style="width:100%">
		  <ul class="nav nav-tabs" style="padding-left:20%">
		  
		    <li 
		    <%
		    if(request.getAttribute("tweet")=="active"){
		    	out.print("class=\"active\"");
		    }%>
		    ><a href="SearchServlet?query=<%=request.getAttribute("q") %>&method=<%=request.getParameter("method") %>&page=<%=1 %>&tweetOrHashtag=tweet" >Tweet</a></li>
		  	<li 
		  	 <%
		    if(request.getAttribute("hashtag")=="active"){
		    	out.print("class=\"active\"");
		    }%>
		  	><a href="SearchServlet?query=<%=request.getAttribute("q") %>&method=<%=request.getParameter("method") %>&page=<%=1 %>&tweetOrHashtag=hashtag">HashTag</a></li>
		  </ul>
		<div class="container-fluid tab-content" style="max-width: 1200px;vertical-align:top;padding-left:20%;padding-right:0;display: inline-block;">
		<%
			tweetArray = (ArrayList<TweetRecord>)request.getAttribute("tweetlist");
			int currentPage=(Integer)request.getAttribute("page");
			int start=(currentPage-1)*20;
			int num=tweetArray.size();
			int pageTotal=(num+19)/20;
			if(num==0)
				out.println("No Result!");
			else{
				int end=(start+20)>num?num:(start+20);
				
				for(int i=start;i<end;i++){
					
			%>
				<div class="row-fluid" style="padding-top:32px;padding-bottom:32px">
					<div class="span12">
						<img src=<%=tweetArray.get(i).getProfileImageURL() %> class="rounded float-xs-left img-thumbnail" style="display: inline-block;" alt="null">
						<span class="float-xs-right" style="display: inline-block;">
						<h3><%=tweetArray.get(i).getName() %>@<%=tweetArray.get(i).getScreenName() %>·
							<small><%=tweetArray.get(i).getCreatT().toString() %></small>
						</h3>
						</span>
						<p>
						</p>
						<blockquote>
							<em><%=tweetArray.get(i).getSnippet() %></em>
						</blockquote> 
						
						<!-- Modal -->
						
						
						<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal<%=i %>">
						  more details
						</button>
						
						<!-- Modal -->
						<div class="modal fade" id="myModal<%=i %>" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" sytle="vertical-align:middle">
						  <div class="modal-dialog" role="document">
						    <div class="modal-content">
						      <div class="modal-header">
						        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						        <h4 class="modal-title" id="myModalLabel<%=i %>">More Details</h4>
						      </div>
						      <div class="modal-body">
						      	<img src=<%=tweetArray.get(i).getProfileImageURL() %> class="rounded float-xs-left img-thumbnail" style="display: inline-block;" alt="null">
								<span class="float-xs-right" style="display: inline-block;">
							        <h3><%=tweetArray.get(i).getName() %>@<%=tweetArray.get(i).getScreenName() %>·
										<small><%=tweetArray.get(i).getCreatT().toString() %></small>
									</h3>
								</span>
								<p>
								</p>
								<blockquote>
									<em><%=tweetArray.get(i).getText() %></em>
								</blockquote>
								<div>
								<img alt="" src="<%=tweetArray.get(i).getMediaURL()%>" style="max-width: 400px;">
								</div>
								<h5>
									<span style="padding-right:16px">retweet:</span><%=tweetArray.get(i).getRetweet() %>
									
									<span>favorite:</span><%=tweetArray.get(i).getFavorite() %>
								</h5> 
								
						      </div>
						      <div class="modal-footer">
						        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						      </div>
						    </div>
						  </div>
						</div>
						
						
					</div>
				</div>
			<%
				}
			%>
			
				<ul class="pagination pagination-lg">
				<%	if(currentPage==1){%>
					<li><a class="disabled" href="#">&laquo;</a></li>
				<%	}else{%>
					<li><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=(currentPage-1) %>&tweetOrHashtag=<%=request.getAttribute("toh") %>">&laquo;</a></li>
				<%	}%>
				    
				<%
					if(currentPage<3){
						for(int i=1;i<6;i++){
							if(i>pageTotal)
								break;
							if(i==currentPage){	%>
								<li class="active"><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=i %>&tweetOrHashtag=<%=request.getAttribute("toh") %>"><%=i %></a></li>
				<% 			}else{	%>
								<li><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=i %>&tweetOrHashtag=<%=request.getAttribute("toh") %>"><%=i %></a></li>
				<%			}
						}
					}else if(currentPage>pageTotal-2 && pageTotal>5){
						for(int i=(pageTotal-4);i<(pageTotal+1);i++){
							if(i==currentPage){	%>
								<li class="active"><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=i %>&tweetOrHashtag=<%=request.getAttribute("toh") %>"><%=i %></a></li>
				<% 			}else{	%>
								<li><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=i %>&tweetOrHashtag=<%=request.getAttribute("toh") %>"><%=i %></a></li>
				<%			}
						}
					}else{
						for(int i=currentPage-2;i<currentPage+3;i++){
							if(i<1)
								continue;
							if(i>pageTotal)
								break;
							if(i==currentPage){	%>
								<li class="active"><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=i %>&tweetOrHashtag=<%=request.getAttribute("toh") %>"><%=i %></a></li>
				<% 			}else{	%>
								<li><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=i %>&tweetOrHashtag=<%=request.getAttribute("toh") %>"><%=i %></a></li>
				<%			}
						}
					}
				
				%>
					<%	
						if(currentPage==pageTotal){%>
						<li><a class="disabled" href="#">&raquo;</a></li>
					<%	}else{%>
						<li><a href="SearchServlet?query=<%=request.getParameter("query") %>&method=<%=request.getParameter("method") %>&page=<%=(currentPage+1) %>&tweetOrHashtag=<%=request.getAttribute("toh") %>">&raquo;</a></li>
					<%	}
					}%>
			   
			</ul>
		</div>
		<div class="container-fluid tab-content" style="vertical-align:top;display: inline-block;padding-left:5%">
			<h2>Related Photos</h2>
			
			<div class="rounded;word-break: break-all;" sytle="overflow:hidden">
			
				
					<div class="divcss5">
					<ul>
					<%
						ArrayList<String> relatedpic = new ArrayList<String>();
						relatedpic=(ArrayList<String>)request.getAttribute("relatedpic");
						int picnum=relatedpic.size();
						for(int ipic=0;ipic<picnum;ipic++){%>
							<li><img alt="" src="<%=relatedpic.get(ipic) %>"></li>
					<%	}
					%>
					
					</ul>

					</div>
			</div>
			
		</div>
		</div>
		
	</body>
</html>