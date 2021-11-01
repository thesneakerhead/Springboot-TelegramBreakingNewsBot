package TelegramBot.TelegramServer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class FlagRegister {
	private Flag everythingFlag;
	private Flag topHeadlinesFlag;
	public FlagRegister()
	{
		this.everythingFlag = Flag.ONE;
		this.topHeadlinesFlag = Flag.ONE;
	}
	public void clearFlags()
	{
		this.everythingFlag = Flag.ONE;
		this.topHeadlinesFlag = Flag.ONE;
	}
	public void clearEverythingFlag()
	{
		this.everythingFlag = Flag.ONE;
	}
	public void clearHeadlinesFlag()
	{
		this.topHeadlinesFlag = Flag.ONE;
	}
}
