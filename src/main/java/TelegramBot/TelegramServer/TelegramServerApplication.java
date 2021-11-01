package TelegramBot.TelegramServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import TelegramBot.TelegramServer.BreakingNewsBot;

@SpringBootApplication
public class TelegramServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramServerApplication.class, args);
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new BreakingNewsBot());
			
		} catch (TelegramApiException e) {
			// TODO: handle exception
		}
	}

}
