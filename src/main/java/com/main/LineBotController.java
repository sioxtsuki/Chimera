package com.main;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.factory.DBFactory;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.utility.Constants;
import com.utility.DBConnection;

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
		System.out.println("request: " + request.getParameter("message").toString());

		@SuppressWarnings("unused")
		BotApiResponse response;


		String value = "<" + request.getParameter("server").toString() + ">\r\n";
		value += request.getParameter("text").toString()+ ">\r\n";
		value += request.getParameter("message").toString()+ ">\r\n";

    	DBConnection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	String text = "";
    	StringBuilder sbFindSQL = null;
    	Resource resource = null;
    	Properties props = null;

		try
		{
        	resource = new ClassPathResource(Constants.PROP_PATH);
        	props = PropertiesLoaderUtils.loadProperties(resource);
			conn = DBFactory.getConnection(props);


			if (value.isEmpty() == false)
			{
				String bot_id = props.getProperty("id").toString();

				// ユーザ情報を取得
				String tb_user = conn.GetProps().getProperty("tb.user");
		    	sbFindSQL = new StringBuilder();
				sbFindSQL.delete(0, sbFindSQL.length());
				sbFindSQL.append("SELECT user_id, authority FROM ");
				sbFindSQL.append(tb_user.toString());
				sbFindSQL.append(" WHERE permissions =? AND bot_id=?");

				ps = conn.getPreparedStatement(sbFindSQL.toString(), null);
				if (ps != null)
				{
					ps.clearParameters();
					ps.setInt(1, 1);
					ps.setString(2, bot_id.toString());
					rs = ps.executeQuery();
					if (rs != null)
					{
						// ラインへプッシュ
						while (rs.next())
						{
							String user_id = rs.getString("user_id").toString();

							response = this.lineMessagingClient
							        .pushMessage(new PushMessage(user_id.toString(),
							                     new TextMessage(value.toString()
							                      ))).get();
						}
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
