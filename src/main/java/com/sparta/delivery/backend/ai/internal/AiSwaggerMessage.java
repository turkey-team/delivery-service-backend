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
				  "id": "e0866968-d417-469a-98e8-7ee6275ce170",
				  "reqMessage": "치즈와 베이컨이 들어간 햄버거 메뉴의 음식 설명을 추천해줘",
				  "resMessage": "고소한 치즈와 짭짤한 베이컨의 환상적인 만남! 풍미 가득한 햄버거",
				  "createdAt": "2025-10-13T06:49:42.431683Z",
				  "createdBy": 1
				}
			  ],
			  "pageable": {
				"pageNumber": 0,
				"pageSize": 1,
				"sort": {
				  "empty": false,
				  "sorted": true,
				  "unsorted": false
				},
				"offset": 0,
				"paged": true,
				"unpaged": false
			  },
			  "last": true,
			  "totalPages": 1,
			  "totalElements": 1,
			  "size": 1,
			  "number": 0,
			  "sort": {
				"empty": false,
				"sorted": true,
				"unsorted": false
			  },
			  "first": true,
			  "numberOfElements": 1,
			  "empty": false
			}
		""";

}
