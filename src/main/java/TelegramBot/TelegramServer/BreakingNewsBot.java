package TelegramBot.TelegramServer;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import TelegramBot.NewsApiPojo.Article;
import TelegramBot.NewsApiPojo.Root;
import TelegramBot.NewsApiPojo.Source;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BreakingNewsBot extends TelegramLongPollingBot {
	private HashMap<Long, FlagRegister> userRegisters;
	
	public BreakingNewsBot()
	{
		userRegisters = new HashMap<Long, FlagRegister>();
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		if (update.hasMessage() && update.getMessage().hasText()) {
	        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
	        Long userId = update.getMessage().getFrom().getId();
	        message.setChatId(userId.toString());
	        String recievedMessage = update.getMessage().getText();
	        FlagRegister fromRegister = userRegisters.get(userId);
	        if (fromRegister==null || recievedMessage.startsWith("/"))
	        {
	        	fromRegister = new FlagRegister();
	        }
	        //when user has not started any action
	        	if (fromRegister.getEverythingFlag().equals(Flag.ONE)&&fromRegister.getTopHeadlinesFlag().equals(Flag.ONE))
	        	{
				        switch (recievedMessage) {
						case "/getNews":
							try {
								getNews().thenApply(apiReply->{
									
									try {
										message.setText(apiReply);
										execute(message);
									} catch (TelegramApiException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									return null;
								});
							} catch (Exception e) {
								
								// TODO Auto-generated catch block
								message.setText("there was an api error");
								try {
									execute(message);
								} catch (TelegramApiException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								e.printStackTrace();
							}
							break;
						case "/test":
							message.setText("for verification");
							try {
								execute(message);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
							break;
						case "/start":
							message.setText("Welcome to Breaking News Bot! enter '/' to see the list of commands");
							try {
								execute(message);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
							break;
						case "/topical":
							fromRegister.setEverythingFlag(Flag.TWO);
							fromRegister.setTopHeadlinesFlag(Flag.ONE);
							userRegisters.put(userId, fromRegister);
							message.setText("Enter a one-word topic of the News you would like to see");
							try {
								execute(message);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
							break;
						case "/bycountry":
							fromRegister.setEverythingFlag(Flag.ONE);
							fromRegister.setTopHeadlinesFlag(Flag.TWO);
							userRegisters.put(userId, fromRegister);
							message.setText("Enter a the letter abbreviation of the country from which you would like to see News about\n"
									+ "ae ar at au be bg br ca ch cn co cu cz de eg fr gb gr hk hu id ie il in it jp kr lt lv ma mx my ng nl no nz ph pl pt ro rs ru sa se sg si sk th tr tw ua us ve za");
							try {
								execute(message);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
							break;
						default:
							break;
						}
	        	} //end if for when user has not started any action
	        	
	        	//when topical was selected and responded with a message
	        	else if (fromRegister.getEverythingFlag().equals(Flag.TWO))
	        	{
	        		fromRegister.clearEverythingFlag();
	        		userRegisters.put(userId, fromRegister);
	        		try {
						getEverythingNewsApi(recievedMessage)
						.thenApply(apiReply->{
							
							try {
								message.setText(apiReply);
								execute(message);
							} catch (TelegramApiException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							return null;
						});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	else if (fromRegister.getTopHeadlinesFlag().equals(Flag.TWO))
	        	{
	        		fromRegister.clearEverythingFlag();
	        		userRegisters.put(userId, fromRegister);
	        		try {
						getCountryNewsApi(recievedMessage)
						.thenApply(apiReply->{
							
							try {
								message.setText(apiReply);
								execute(message);
							} catch (TelegramApiException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							return null;
						});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	     }
		
	}

	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return "ENTER BOT NAME HERE";
		
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return "ENTER BOT TOKEN HERE";
	}
	
	public CompletableFuture<String> getNews() throws IOException
	{	CompletableFuture<String> completableFuture;
		completableFuture= CompletableFuture.supplyAsync(()->postNewsRequest());
		return completableFuture;
	}
	public CompletableFuture<String> getEverythingNewsApi(String message) throws IOException
	{	CompletableFuture<String> completableFuture;
		completableFuture= CompletableFuture.supplyAsync(()->NewsApiEverythingRequest(message));
		return completableFuture;
	}
	public CompletableFuture<String> getCountryNewsApi(String message) throws IOException
	{	CompletableFuture<String> completableFuture;
		completableFuture= CompletableFuture.supplyAsync(()->NewsApiByCountryRequest(message));
		return completableFuture;
	}
	public String NewsApiEverythingRequest(String message)
	{
		OkHttpClient client = new OkHttpClient();
		HttpUrl.Builder httpBuilder = HttpUrl.parse("https://newsapi.org/v2/everything").newBuilder();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("apiKey", "API KEY");
		params.put("q", message);
	    if (params != null) {
	       for(Map.Entry<String, String> param : params.entrySet()) {
	           httpBuilder.addQueryParameter(param.getKey(),param.getValue());
	       }
	    }
		Request request = new Request.Builder()
				.url(httpBuilder.build())
				.get()
				.build();
		try(Response response = client.newCall(request).execute())
		{
			ResponseBody body = response.body();
			String responseString = body.string();
			return processStringFromNewsApi(responseString,message);
			
		} catch (IOException e) {
			
			return "request failed";
			
		}
		
	}
	public String NewsApiByCountryRequest(String country)
	{
		OkHttpClient client = new OkHttpClient();
		HttpUrl.Builder httpBuilder = HttpUrl.parse("https://newsapi.org/v2/top-headlines").newBuilder();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("apiKey", "API KEY");
		params.put("country", country);
	    if (params != null) {
	       for(Map.Entry<String, String> param : params.entrySet()) {
	           httpBuilder.addQueryParameter(param.getKey(),param.getValue());
	       }
	    }
		Request request = new Request.Builder()
				.url(httpBuilder.build())
				.get()
				.build();
		try(Response response = client.newCall(request).execute())
		{
			ResponseBody body = response.body();
			String responseString = body.string();
			return processStringFromNewsApi(responseString,country);
			
		} catch (IOException e) {
			
			return "request failed";
			
		}
	}
	public String processStringFromNewsApi(String responseString,String message)
	{
		ObjectMapper oMapper = new ObjectMapper();
		TelegramBot.NewsApiPojo.Root root = null;
		try {
			root = oMapper.readValue(responseString, TelegramBot.NewsApiPojo.Root.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<TelegramBot.NewsApiPojo.Article> articles = root.articles;
		List<TelegramBot.NewsApiPojo.Article> top10Articles = articles.subList(0, 5);
		String retString = "Top 5 articles: "+message;
		retString = retString + "\n===========================";
		retString = retString + "\n";
		for(Article article:top10Articles)
		{
			retString = retString+ article.title+"\n";
			retString = retString+ article.url +"\n";
			retString = retString+"\n";
		}
		return retString;
	}
	public String postNewsRequest()
	{
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://google-news.p.rapidapi.com/v1/top_headlines?lang=en&country=US")
				.get()
				.addHeader("x-rapidapi-host", "google-news.p.rapidapi.com")
				.addHeader("x-rapidapi-key", "API KEY")
				.build();
		try(Response response = client.newCall(request).execute())
		{
			//System.out.println(response.body().string());
			ResponseBody body = response.body();
			String responseString = body.string();
			//JSONParser parser = new JSONParser();
			//JSONArray jsonArray = (JSONArray)parser.parse(responseString);
			ObjectMapper oMapper = new ObjectMapper();
			TelegramBot.TelegramServer.Root root = oMapper.readValue(responseString, TelegramBot.TelegramServer.Root.class);
			List<TelegramBot.TelegramServer.Article> articles = root.articles;
			List<TelegramBot.TelegramServer.Article> top10Articles = articles.subList(0, 4);
			String retString = "Top 5 articles in the world";
			retString = retString + "\n===========================";
			retString = retString + "\n";
			for(TelegramBot.TelegramServer.Article article:top10Articles)
			{
				retString = retString+ article.title+"\n";
				retString = retString+ article.link +"\n";
				retString = retString+"\n";
			}
			return retString;
		}
		catch(JsonParseException e1)
		{
			return "cannot parse";
		}
		catch (IOException e) {
			return "request failed";
		}
	}
	
	
	
    
}
