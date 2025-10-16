package com.sparta.delivery.backend.ai.internal;

public class AiSwaggerMessage {

	public static final String AI_INVALID_JSON = """
			{
			  "errorMessage": "잘못된 JSON 형식입니다.",
			  "statusCode": 400
			}
		""";

	public static final String AI_REQUEST_EMPTY = """
			{
			   "errorMessage": "요청 메세지가 비어있습니다.",
			   "statusCode": 400
			}
		""";

	public static final String AI_REQUEST_TOO_LONG = """
			{
			  "errorMessage": "요청 메세지는 최대 200자까지 가능합니다.",
			  "statusCode": 400
			}
		""";

	public static final String AI_FORBIDDEN = """
			{
			  "errorMessage": "권한이 없습니다.",
			  "statusCode": 403
			}
		""";

	public static final String AI_PROMPT_LIST_EXAMPLE = """
			{
		    "content": [
		      {
		        "id": "66201786-2dfa-4696-87d1-72a32f6f78f0",
		        "reqMessage": "치즈와 베이컨이 들어간 햄버거 메뉴의 음식 설명을 추천해줘",
		        "resMessage": "고소한 치즈와 짭짤한 베이컨의 환상적인 만남! 육즙 가득 패티와 완벽 조화!\\n",
		        "createdAt": "2025-10-16T03:48:10.827628Z",
		        "createdBy": 1
		      }
		    ],
		    "currentPage": 0,
		    "pageSize": 20,
		    "totalElements": 1,
		    "totalPages": 1,
		    "first": true,
		    "last": true,
		    "hasNext": false
		  }
		""";

}
