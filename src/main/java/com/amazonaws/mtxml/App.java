package com.amazonaws.mtxml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
		try {
			context.getLogger().log("input: " + input.toString());
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "text/xml");

			Map<String, String> queryStringParameters = input.getQueryStringParameters();
			String mtMessage = queryStringParameters.get("MTMessage");
			if ((mtMessage == null) || mtMessage.equals("")) {
				throw new IllegalArgumentException("MTMessage parameter must be supplied.");
			}
			String xmlMsg = mtToXml(mtMessage);

			APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);
			return response.withStatusCode(200).withBody(xmlMsg);

		} catch (Exception e) {
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "text/plain");
			String body = e.getMessage();
			int statusCode = 500;
			return new APIGatewayProxyResponseEvent().withBody(body).withStatusCode(statusCode);
		}

	}

	private static String mtToXml(String mtMessage) throws IOException, UnknownTagException {
		// Replace '\n' with actual newlines
		final Pattern pattern = Pattern.compile("\\\\n", Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(mtMessage);

		// The substituted value will be contained in the result variable
		mtMessage = matcher.replaceAll("\n");
		return new Mt(mtMessage).toXml();
	}

}
