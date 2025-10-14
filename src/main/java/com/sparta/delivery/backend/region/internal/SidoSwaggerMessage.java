package com.sparta.delivery.backend.region.internal;

public class SidoSwaggerMessage {

	public static final String SIDO_INVALID_JSON = """
			{
				"errorMessage": "잘못된 JSON 형식입니다.",
				"statusCode": 400
			}
		""";

	public static final String SIDO_REQUEST_NAME_EMPTY = """
			{
		    	"errorMessage": "시/도 이름은 필수입니다.",
		    	"statusCode": 400
		  	}
		""";

	public static final String SIDO_REQUEST_NAME_INVALID = """
			{
		     	"errorMessage": "시/도 이름은 한글, 숫자, 하이픈(-)만 사용할 수 있으며, 최대 50자까지 가능합니다.",
		    	"statusCode": 400
		 	}
		""";

	public static final String SIDO_REQUEST_NAME_DUPLICATE = """
			{
				"errorMessage": "요청에 중복된 시/도 이름이 포함되어 있습니다.",
				"statusCode": 400
		   	}
		""";

	public static final String SIDO_REQUEST_CODE_EMPTY = """
			{
		    	"errorMessage": "시/도 코드는 필수입니다.",
		    	"statusCode": 400
		  	}
		""";

	public static final String SIDO_REQUEST_CODE_INVALID = """
			{
		    	"errorMessage": "시/도 코드는 2자의 숫자만 가능합니다.",
		    	"statusCode": 400
			}
		""";

	public static final String SIDO_REQUEST_CODE_DUPLICATE = """
			{
			  	"errorMessage": "요청에 중복된 시/도 코드가 포함되어 있습니다.",
				"statusCode": 400
		   	}
		""";

	public static final String SIDO_FORBIDDEN = """
			{
			  	"errorMessage": "권한이 없습니다.",
			  	"statusCode": 403
			}
		""";

	public static final String SIDO_NOT_FOUND = """
			{
			  	"errorMessage": "존재하지 않는 시/도입니다.",
			  	"statusCode": 404
			}
		""";

	public static final String SIDO_REQUEST_NAME_EXISTS = """
			{
		       	"errorMessage": "이미 존재하는 시/도 이름이 포함되어 있습니다.",
		       	"statusCode": 409
		    }
		""";

	public static final String SIDO_REQUEST_CODE_EXISTS = """
			{
		       	"errorMessage": "이미 존재하는 시/도 코드가 포함되어 있습니다.",
		       	"statusCode": 409
		    }
		""";

}
