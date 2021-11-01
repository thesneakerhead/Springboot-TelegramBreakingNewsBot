package TelegramBot.TelegramServer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {
	@GetMapping("/preventIdle")
	public String ping()
	{
		return "app pinged!";
	}
}
