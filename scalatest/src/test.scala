import org.scribe.builder._
import org.scribe.builder.api._
import org.scribe.model._
import org.scribe.oauth._

import scala.util.parsing.json._

object test {

	def main(args: Array[String]) {
		val CONSUMER_KEY = "kNAWebRap9d6Zl5UYRXvw"
		val CONSUMER_SECRET = "8OnqIdFPcRt4K73ApXKV2JiLm8XShtzNXkYt9UOEvrY"
		
		val TIMELINE_HOME = "http://api.twitter.com/1/statuses/home_timeline.json"
			

		val service = new ServiceBuilder().provider(classOf[TwitterApi]).
			apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build	

		val requestToken = service.getRequestToken()

		val auth_url = service.getAuthorizationUrl(requestToken);  			println("Go and authorize omegafun with this URL: " + auth_url)
		
		print("Input verification code here: ")
		val code = Console.readLine;										println("Code: " + code)
		val verifier = new Verifier(code)
		
		val accessToken = service.getAccessToken(requestToken, verifier); 	println("The access token looks like this:" + accessToken)
		
		val req = new OAuthRequest(Verb.GET, TIMELINE_HOME)
		service.signRequest(accessToken, req)
		val resp = req.send
		val body = resp.getBody
		
		printToFile("tweets.txt", body)
		
		val parsed = JSON.parseRaw(body)
		print(pprint(parsed, 0))
		
		//val tweets = ("""\"text)
		
	}
	
	def pprint(j: Option[Any], l:Int):String = {
		val indent = (for(i <- List.range(0, l)) yield "  ").mkString
		j match{
			case Some(o:JSONObject) => {			
				/*
				o.obj.get("user") match {
					case Some(u:JSONObject) => {
						pprint(u.obj.get("screen_name"), 0) + " -> " + pprint(o.obj.get("text"), 0)
					}
					case _ => "undefined"
				}
				
				*/ 
				List("{",
				o.obj.keys.map(key => indent + "  " + "\"" + key + "\":" + pprint(o.obj.get(key), l + 1)).mkString(",\n"),
				indent + "}").mkString("\n")
				
			}
			case Some(a:JSONArray) => {
				List("[",
				a.list.map(v => indent + "  " + pprint(Some(v), l + 1)).mkString(",\n"),
				indent + "]").mkString("\n")
			}
			case Some(s: String) => "\"" + s + "\""
			case Some(n: Number) => n.toString
			case None => "null"
			case _ => "undefined"
		}
	}
	
	def printToFile(fname: String, str: String) {
		val p = new java.io.PrintWriter(fname)
		try { p.print(str) } finally { p.close() }
	}
	
	def prettyPrintTweets() {
	}
}