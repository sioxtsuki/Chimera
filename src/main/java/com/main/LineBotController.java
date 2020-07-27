package com.main;

import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;

/**
 * @author shiotsuki
 *
 */
@RestController
public class LineBotController {

	@Autowired
	private LineMessagingClient lineMessagingClient;


	@RequestMapping(value = "/linebot")
	void index(HttpServletRequest request) throws RuntimeException {

		System.out.println("request: " + request.getParameter("server").toString());
		System.out.println("request: " + request.getParameter("text").toString());

		@SuppressWarnings("unused")
		BotApiResponse response;


		String value = "<" + request.getParameter("server").toString() + ">\r\n";
		value.concat(request.getParameter("text").toString());

		try {
			response = this.lineMessagingClient
			        .pushMessage(new PushMessage("Ud6e699869888126beedab30c5b3d484e".toString(),
			                     new TextMessage(value.toString()
			                      ))).get();

		} catch (InterruptedException | ExecutionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
