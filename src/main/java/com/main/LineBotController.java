package com.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.utility.Constants;

/**
 * @author shiotsuki
 *
 */
@RestController
public class LineBotController {

	@Autowired
	private LineMessagingClient lineMessagingClient;


	/**
	 * プッシュ処理
	 * @param request
	 * @throws RuntimeException
	 */
	@RequestMapping(value = "/linebot")
	void index(HttpServletRequest request) throws RuntimeException {

		@SuppressWarnings("unused")
		BotApiResponse response;

		//+------------------------------+
		//| 停止の場合はアラートを受け付けない
		//+------------------------------+
		// Delete By 2024.07.08
		/*
		int ret = Utility.GetRateCheckState();
		if (ret == 0) // 配信を許可しない場合は処理中断
		{
			return;
		}*/


		// プロパティ情報を取得　
    	Properties conf_props = new Properties();
    	try {
			conf_props.load(new FileInputStream(Constants.CONF_PROP_PATH));
		} catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
    	if (conf_props.getProperty("ratechk.allow").toString().equals("0") == true) // 配信を許可しない場合は処理中断
    	{
    		return;
    	}

		String strServer = request.getParameter("server");
		String strText = request.getParameter("text");
		String strMessage = request.getParameter("message");

		System.out.println("request: " + strServer);
		System.out.println("request: " + strText);
		System.out.println("request: " + strMessage);

		String value = "";
		if (strServer != null)
			if (strServer.isEmpty() == false)
				value = "<" + strServer.toString() + ">\r\n";
		if (strText != null)
			if (strText.isEmpty() == false)
				value += strText.toString()+ "\r\n";
		if (strMessage != null)
			if (strMessage.isEmpty() == false)
				value += strMessage.toString()+ "\r\n";

		// メッセージが無い場合は終了
		if (value.isEmpty() == true) return;

		// Delete By 2024.07.08 Start
    	//DBConnection conn = null;
    	//PreparedStatement ps = null;
    	//ResultSet rs = null;
    	//StringBuilder sbFindSQL = null;
		// Delete By 2024.07.08 End

    	Resource resource = null;
    	Properties props = null;

		try
		{
			resource = new ClassPathResource(Constants.PROP_PATH);
        	props = PropertiesLoaderUtils.loadProperties(resource);

        	String user_id = props.getProperty("groupid").toString();
			response = this.lineMessagingClient
			        .pushMessage(new PushMessage(user_id.toString(),
			                     new TextMessage(value.toString()
			                      ))).get();


			/* Delete By 2024.07.08 Start
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
			Delete By 2024.07.08 End */

		} catch (InterruptedException | ExecutionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		//} catch (InstantiationException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
		//} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
		//} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
		}
		finally
		{
		}
	}
}
