package com.sparta.delivery.backend.region.internal;

public class DongSwaggerMessage {

	public static final String DONG_INVALID_JSON = """
			{
				"errorMessage": "잘못된 JSON 형식입니다.",
				"statusCode": 400
			}
		""";

	public static final String DONG_REQUEST_NAME_EMPTY = """
			{
		    	"errorMessage": "동 이름은 필수입니다.",
		    	"statusCode": 400
		  	}
		""";

	public static final String DONG_REQUEST_NAME_INVALID = """
			{
		     	"errorMessage": "동 이름은 한글, 숫자, 하이픈(-)만 사용할 수 있으며, 최대 50자까지 가능합니다.",
		    	"statusCode": 400
		 	}
		""";

	public static final String DONG_REQUEST_NAME_DUPLICATE = """
			{
				"errorMessage": "요청에 중복된 동 이름이 포함되어 있습니다.",
				"statusCode": 400
		   	}
		""";

	public static final String DONG_REQUEST_CODE_EMPTY = """
			{
		    	"errorMessage": "동 코드는 필수입니다.",
		    	"statusCode": 400
		  	}
		""";

	public static final String DONG_REQUEST_CODE_INVALID = """
			{
		    	"errorMessage": "동 코드는 3자의 숫자만 가능합니다.",
		    	"statusCode": 400
			}
		""";

	public static final String DONG_REQUEST_CODE_DUPLICATE = """
			{
			  	"errorMessage": "요청에 중복된 동 코드가 포함되어 있습니다.",
				"statusCode": 400
		   	}
		""";

	public static final String DONG_FORBIDDEN = """
			{
			  	"errorMessage": "권한이 없습니다.",
			  	"statusCode": 403
			}
		""";

	public static final String DONG_NOT_FOUND = """
			{
			  	"errorMessage": "존재하지 않는 동입니다.",
			  	"statusCode": 404
			}
		""";

	public static final String DONG_REQUEST_NAME_EXISTS = """
			{
		       	"errorMessage": "이미 존재하는 동 이름이 포함되어 있습니다.",
		       	"statusCode": 409
		    }
		""";

	public static final String DONG_REQUEST_CODE_EXISTS = """
			{
		       	"errorMessage": "이미 존재하는 동 코드가 포함되어 있습니다.",
		       	"statusCode": 409
		    }
		""";

}
