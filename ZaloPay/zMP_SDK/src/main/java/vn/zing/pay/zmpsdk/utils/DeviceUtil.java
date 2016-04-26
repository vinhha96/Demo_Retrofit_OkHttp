/**
 * Copyright © 2015 by VNG Corporation
 * All rights reserved. No part of this publication may be reproduced, distributed, 
 * or transmitted in any form or by any means, including photocopying, recording, 
 * or other electronic or mechanical methods, without the prior written permission 
 * of the publisher, except in the case of brief quotations embodied in critical reviews 
 * and certain other noncommercial uses permitted by copyright law.
 *
 * Project: ZingPaySDK
 * File: vn.zing.pay.zmpsdk.utils.DeviceUtil.java
 * Created date: Dec 23, 2015
 * Owner: YenNLH
 */
package vn.zing.pay.zmpsdk.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import vn.zing.pay.zmpsdk.data.GlobalData;
import vn.zing.pay.zmpsdk.data.SharedPreferencesManager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * @author YenNLH
 * 
 */
public class DeviceUtil {
	public static String getUniqueDeviceID(final Context owner) {
		String uuid = null;
		try {
			if (SharedPreferencesManager.getInstance().getSharedPreferences() != null)
				uuid = SharedPreferencesManager.getInstance().getUDID();
		} catch (Exception ex) {
		}

		if (TextUtils.isEmpty(uuid)) {
			TelephonyManager telephonyManager = (TelephonyManager) owner.getSystemService(Context.TELEPHONY_SERVICE);
			uuid = telephonyManager.getDeviceId();
		}

		if (TextUtils.isEmpty(uuid)) {
			// can't get device id, get from share pref
			uuid = SharedPreferencesManager.getInstance().getUDID();

			// share pref is null gen and save
			if (TextUtils.isEmpty(uuid)) {
				uuid = UUID.randomUUID().toString();
				SharedPreferencesManager.getInstance().setUDID(uuid);
			}
		}
		return uuid;
	}

	/**
	 * Get version code of owner application
	 * 
	 * @return Version code interger
	 */
	public static int getAppVersion() {
		try {
			PackageInfo packageInfo = GlobalData.getApplication().getPackageManager()
					.getPackageInfo(GlobalData.getApplication().getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (Exception e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Returns the consumer friendly device name
	 * 
	 * @return Friendly device name
	 */
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		}
		return capitalize(manufacturer) + " " + model;
	}

	private static String capitalize(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		char[] arr = str.toCharArray();
		boolean capitalizeNext = true;
		String phrase = "";
		for (char c : arr) {
			if (capitalizeNext && Character.isLetter(c)) {
				phrase += Character.toUpperCase(c);
				capitalizeNext = false;
				continue;
			} else if (Character.isWhitespace(c)) {
				capitalizeNext = true;
			}
			phrase += c;
		}
		return phrase;
	}

	public static String getCertificateSHA1Fingerprint(Context pContext) {
		PackageManager pm = pContext.getPackageManager();
		String packageName = pContext.getPackageName();
		int flags = PackageManager.GET_SIGNATURES;
		PackageInfo packageInfo = null;
		String hexString = null;

		try {
			packageInfo = pm.getPackageInfo(packageName, flags);

			Signature[] signatures = packageInfo.signatures;
			byte[] cert = signatures[0].toByteArray();
			InputStream input = new ByteArrayInputStream(cert);
			CertificateFactory certificateFactory = null;

			certificateFactory = CertificateFactory.getInstance("X509");

			X509Certificate c = (X509Certificate) certificateFactory.generateCertificate(input);

			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(c.getEncoded());
			hexString = HexStringUtil.byteArrayToHexString(publicKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hexString;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	public static void copyToClipboard(Context pContext, String pText) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) pContext
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(pText);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) pContext
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", pText);
			clipboard.setPrimaryClip(clip);
		}
	}
}
