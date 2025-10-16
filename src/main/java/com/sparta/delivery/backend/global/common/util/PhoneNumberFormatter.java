package com.sparta.delivery.backend.global.common.util;


public class PhoneNumberFormatter {
	public static String formatPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			return phoneNumber;
		}

		int length = phoneNumber.length();

		// 0XX-XXXX-XXXX (11자리)
		if (length == 11) {
			return phoneNumber.substring(0, 3) + "-" +
				phoneNumber.substring(3, 7) + "-" +
				phoneNumber.substring(7);
		}

		// 02-XXXX-XXXX (10자리)
		else if (phoneNumber.startsWith("02") && length == 10) {
			return phoneNumber.substring(0, 2) + "-" +
				phoneNumber.substring(2, 6) + "-" +
				phoneNumber.substring(6);
		}

		// 0XX-XXX-XXXX (10자리)
		else if (length == 10) {
			return phoneNumber.substring(0, 3) + "-" +
				phoneNumber.substring(3, 6) + "-" +
				phoneNumber.substring(6);
		}

		// 02-XXX-XXXX (9자리)
		else if (phoneNumber.startsWith("02") && length == 9) {
			return phoneNumber.substring(0, 2) + "-" +
				phoneNumber.substring(2, 5) + "-" +
				phoneNumber.substring(5);
		}

		return phoneNumber;
	}
}
