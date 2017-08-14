package vn.com.vng.zalopay.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import vn.com.vng.zalopay.data.util.NameValuePair;
import vn.com.vng.zalopay.data.util.PhoneUtil;
import vn.com.vng.zalopay.data.util.Strings;

/**
 * Created by huuhoa on 7/5/16.
 */
@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 16, application = AndroidApplicationTest.class)
public class StringsTest {

    @Test
    public void testStripAccents() throws Exception {
        String input = "abcd";
        String output = "abcd";

        String value = Strings.stripAccents(input);
        Assert.assertTrue(output.equals(value));

        input = "Cộng hoà xã hội chủ nghĩa Việt Nam - Độc lập tự do hạnh phúc";
        output = "Cong hoa xa hoi chu nghia Viet Nam - Doc lap tu do hanh phuc";

        value = Strings.stripAccents(input);
        Assert.assertTrue(output.equals(value));
    }

    @Test
    public void testJoinWithDelimiter() throws Exception {
        List<String> input = new ArrayList<>();
        input.add("1");
        input.add("2");
        input.add("3");
        String expected = "1|2|3";

        String output = Strings.joinWithDelimiter("|", input);
        Assert.assertTrue(expected.equals(output));
    }


    @Test
    public void testJoinLongListWithDelimiter() throws Exception {
        List<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        input.add(3L);
        String expected = "1|2|3";

        String output = Strings.joinWithDelimiter("|", input);
        Assert.assertTrue(expected.equals(output));
    }

    @Test
    public void testStripLeadingPath() {
        String input = "../../fonts/zalopay.ttf";
        String expected = "fonts/zalopay.ttf";

        String output = Strings.stripLeadingPath(input);

        Assert.assertEquals(expected, output);
        Assert.assertEquals("", Strings.stripLeadingPath(null));
        Assert.assertEquals("", Strings.stripLeadingPath(""));
        Assert.assertEquals("", Strings.stripLeadingPath(".."));
        Assert.assertEquals("", Strings.stripLeadingPath("../"));
        Assert.assertEquals("", Strings.stripLeadingPath("/.."));
        Assert.assertEquals("", Strings.stripLeadingPath("../.."));
        Assert.assertEquals("", Strings.stripLeadingPath("../../.."));
        Assert.assertEquals("", Strings.stripLeadingPath("/"));
        Assert.assertEquals("", Strings.stripLeadingPath("/////.."));
        Assert.assertEquals("fonts", Strings.stripLeadingPath("fonts"));
        Assert.assertEquals("main.jsbundle", Strings.stripLeadingPath("main.jsbundle"));
        Assert.assertEquals("drawable/abc/../abc.png", Strings.stripLeadingPath("drawable/abc/../abc.png"));
    }

    @Test
    public void testGetDomainName() throws Exception {
        String string1 = "https://zpdemo.github.io/vibrate.html";
        String string2 = "https://facebook.com";
        String string3 = "http://drive.google.com/bla/bla/bla";
        String string4 = "http://www.stackoverflow.com/questions";
        String string5 = "http://www-01.hopperspot.com";
        String string6 = "http://wwwsupernatural-brasil.blogspot.com";
        String string7 = "http://zoyanailpolish.blogspot.com";
        String string8 = "http://ww.socialrating.it";
        String string9 = "http://example.co.uk";
        String string10 = "http://sub1.somesite3.com";
        String string11 = "http://1.2.com";
        String string12 = "http://a.b.c.d";
        String string13 = "http://www.zalopay.com.vn";
        String string14 = "https://bbc.co.uk/vietnamese";
        String string15 = "https://www.google.com";
        String string16 = "https://github.com/segunfamisa/bottom-navigation-demo";
        String string17 = "https://gitlab.zalopay.vn/zalopay-apps/app-ios";
        String string18 = "https://developer.android.com/studio/profile/optimize-ui.html";
        String string19 = "https://x.y.z.com/";
        String string20 = "https://c.d/";

        Assert.assertEquals("github.io", Strings.getDomainName(string1));
        Assert.assertEquals("facebook.com", Strings.getDomainName(string2));
        Assert.assertEquals("google.com", Strings.getDomainName(string3));
        Assert.assertEquals("stackoverflow.com", Strings.getDomainName(string4));
        Assert.assertEquals("hopperspot.com", Strings.getDomainName(string5));
        Assert.assertEquals("blogspot.com", Strings.getDomainName(string6));
        Assert.assertEquals("blogspot.com", Strings.getDomainName(string7));
        Assert.assertEquals("socialrating.it", Strings.getDomainName(string8));
        Assert.assertEquals("example.co.uk", Strings.getDomainName(string9));
        Assert.assertEquals("somesite3.com", Strings.getDomainName(string10));
        Assert.assertEquals("2.com", Strings.getDomainName(string11));
        Assert.assertEquals("c.d", Strings.getDomainName(string12));
        Assert.assertEquals("zalopay.com.vn", Strings.getDomainName(string13));
        Assert.assertEquals("bbc.co.uk", Strings.getDomainName(string14));
        Assert.assertEquals("google.com", Strings.getDomainName(string15));
        Assert.assertEquals("github.com", Strings.getDomainName(string16));
        Assert.assertEquals("zalopay.vn", Strings.getDomainName(string17));
        Assert.assertEquals("android.com", Strings.getDomainName(string18));
        Assert.assertEquals("z.com", Strings.getDomainName(string19));
        Assert.assertEquals("c.d", Strings.getDomainName(string20));
    }

    @Test
    public void testInvalidGetDomainName() throws Exception {
        String invalid1 = "zpdemo.github.io/vibrate.html";
        String invalid2 = "www.qr-code-generator.com";

        Assert.assertEquals("", Strings.getDomainName(invalid1));
        Assert.assertEquals("", Strings.getDomainName(invalid2));
    }

    @Test
    public void testGetIndexOfSearchString() throws Exception {
        String string = "Thông tin tài khoản";
        String search1 = "ông";
        String search2 = "n";
        String search3 = "tin tài";
        String search4 = "m";
        String search5 = "ong";

        Assert.assertEquals(2, Strings.getIndexOfSearchString(string, search1));
        Assert.assertEquals(3, Strings.getIndexOfSearchString(string, search2));
        Assert.assertEquals(6, Strings.getIndexOfSearchString(string, search3));
        Assert.assertEquals(-1, Strings.getIndexOfSearchString(string, search4));
        Assert.assertEquals(2, Strings.getIndexOfSearchString(string, search5));
    }

    @Test
    public void testGetRawQueryMultipleValue() throws Exception {
        Assert.assertEquals("(?,?,?)", Strings.getRawQueryMultipleValue("a", "b", "c"));
        Assert.assertEquals("(?,?,?,?,?)", Strings.getRawQueryMultipleValue("a", "b", "c", "d", "d"));
        Assert.assertEquals("(?)", Strings.getRawQueryMultipleValue("a"));
    }

    @Test
    public void testNullInputParseNameValuePair() {
        Assert.assertNotEquals("Ensure not return null", null, Strings.parseNameValues(null));
        ArrayList<NameValuePair> emptyList = new ArrayList<>();
        Assert.assertArrayEquals("Ensure return empty list", emptyList.toArray(), Strings.parseNameValues(null).toArray());
        Assert.assertArrayEquals("Ensure return empty list", emptyList.toArray(), Strings.parseNameValues("").toArray());
    }

    @Test
    public void testInvalidInputParseNameValuePair() {
        ArrayList<NameValuePair> emptyList = new ArrayList<>();
        Assert.assertArrayEquals("Ensure return empty list for invalid input", emptyList.toArray(), Strings.parseNameValues("00").toArray());
    }

    @Test
    public void testValidInputParseNameValuePair() {
        ArrayList<NameValuePair> expected = new ArrayList<>();
        expected.add(new NameValuePair("key", "value"));
        ArrayList<NameValuePair> actual = Strings.parseNameValues("key:value");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());

        expected = new ArrayList<>();
        expected.add(new NameValuePair("key", "value"));
        actual = Strings.parseNameValues("key:value\t");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());

        expected = new ArrayList<>();
        expected.add(new NameValuePair("key", "value"));
        actual = Strings.parseNameValues("key:value\tkey1:");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());

        expected = new ArrayList<>();
        expected.add(new NameValuePair("key", "value"));
        expected.add(new NameValuePair("key2", "v2"));
        actual = Strings.parseNameValues("key:value\tkey1:\tkey2:v2");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());

        expected = new ArrayList<>();
        expected.add(new NameValuePair("Nhà mạng", "Viettel"));
        expected.add(new NameValuePair("Mệnh giá", "50.000 VND"));
        expected.add(new NameValuePair("Nạp cho", "Số của tôi - 0902167233"));
        actual = Strings.parseNameValues("Nhà mạng:Viettel\tMệnh giá:50.000 VND\tNạp cho:Số của tôi - 0902167233");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());

        expected = new ArrayList<>();
        expected.add(new NameValuePair("Nhà mạng", "Viettel"));
        expected.add(new NameValuePair("Mệnh giá", "50.000 VND"));
        expected.add(new NameValuePair("Nạp cho", "Số của tôi - 0902167233:123"));
        actual = Strings.parseNameValues("Nhà mạng  :  Viettel   \t  Mệnh giá     : 50.000 VND\tNạp cho : Số của tôi - 0902167233:123");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }


    @Test
    public void testCompareNameValuePair() {
        NameValuePair v1 = new NameValuePair("key", "value");
        NameValuePair v2 = new NameValuePair("key", "value");
        Assert.assertEquals(v1, v2);
    }

    @Test
    public void testAddUrlQueryParams() {
        Map<String, String> map = new TreeMap<>();
        map.put("appid", "10");
        map.put("apptransid", "17061800001");
        map.put("zptransid", "170618100000");

        String url = "https://www.tiki.vn/showitem?from=zalopay";
        String expected = "https://www.tiki.vn/showitem?from=zalopay&appid=10&apptransid=17061800001&zptransid=170618100000";
        String actual = Strings.addUrlQueryParams(url, map);
        Assert.assertEquals(expected, actual);

        url = "https://www.tiki.vn/showitem?from=zalopay#abc";
        expected = "https://www.tiki.vn/showitem?from=zalopay&appid=10&apptransid=17061800001&zptransid=170618100000#abc";
        actual = Strings.addUrlQueryParams(url, map);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEncodeDecodeUTF16() {
        String text1 = "◠‿◠ Mầu \uD83E\uDD17\uD83E\uDD84\uD83E\uDD81\uD83E\uDD81";
        String text2 = "123456";
        String text3 = "AbCd EFch";
        String text4 = "Cộng hoà xã hội chủ nghĩa Việt Nam - Độc lập tự do hạnh phúc";
        String text5 = "\uD83D\uDE03\uD83D\uDE0C\uD83D\uDE06\uD83D\uDE0A\uD83D\uDE06\uD83D\uDE0C\uD83D\uDE0A☺\uD83D\uDE1C\uD83D\uDE0A\uD83D\uDE19\uD83D\uDE1C\uD83D\uDE0A☺\uD83C\uDF50\uD83C\uDF4E\uD83E\uDDC0\uD83C\uDF51\uD83C\uDF4E\uD83E\uDDC0\uD83C\uDF4E\uD83C\uDF50\uD83E\uDDC0\uD83C\uDF4E\uD83C\uDF50\uD83C\uDFD1\uD83C\uDFC2\uD83C\uDFB3\uD83C\uDFB3\uD83C\uDFD1\uD83C\uDFC2\uD83C\uDFB1\uD83C\uDF80\uD83C\uDF90\uD83C\uDF8D\uD83C\uDFAB\uD83C\uDF8D\uD83C\uDF91\uD83C\uDF91⛲\uD83C\uDFED\uD83C\uDFE9⛺\uD83C\uDFED\uD83D\uDD4B\uD83D\uDEBF\uD83D\uDECF\uD83D\uDEBF\uD83C\uDFB0\uD83D\uDECF\uD83D\uDC34\uD83D\uDC29\uD83D\uDC08\uD83D\uDC29\uD83D\uDC04\uD83D\uDC08\uD83C\uDF14\uD83C\uDF0D\uD83D\uDDFA\uD83C\uDF14\uD83D\uDCF2\uD83D\uDEB0\uD83D\uDEE2\uD83C\uDF11\uD83D\uDDFA\uD83C\uDF1A\uD83C\uDF13\uD83C\uDF26\uD83C\uDF08\uD83C\uDF2A\uD83C\uDF00☃\uD83C\uDF2A⛱\uD83D\uDD25\uD83C\uDF0A\uD83C\uDF0A\uD83D\uDCA7\uD83D\uDCF1\uD83D\uDD08\uD83D\uDCF2\uD83D\uDD0B\uD83D\uDD14";

        Assert.assertEquals(text1, Strings.decodeUTF16(Strings.encodeUTF16(text1)));
        Assert.assertEquals(text2, Strings.decodeUTF16(Strings.encodeUTF16(text2)));
        Assert.assertEquals(text3, Strings.decodeUTF16(Strings.encodeUTF16(text3)));
        Assert.assertEquals(text4, Strings.decodeUTF16(Strings.encodeUTF16(text4)));
        Assert.assertEquals(text5, Strings.decodeUTF16(Strings.encodeUTF16(text5)));
    }

    @Test
    public void removeSpecialCharactersTest() {
        String text1 = "◠‿◠ Mầu \uD83E\uDD17\uD83E\uDD84\uD83E\uDD81\uD83E\uDD81";
        String text2 = "123456";
        String text3 = "AbCd EFch";
        String text4 = "Cộng hoà xã hội chủ nghĩa Việt Nam - Độc lập tự do hạnh phúc";
        String text5 = "\uD83D\uDE03\uD83D\uDE0C\uD83D\uDE06\uD83D\uDE0A\uD83D\uDE06\uD83D\uDE0C\uD83D\uDE0A☺\uD83D\uDE1C\uD83D\uDE0A\uD83D\uDE19\uD83D\uDE1C\uD83D\uDE0A☺\uD83C\uDF50\uD83C\uDF4E\uD83E\uDDC0\uD83C\uDF51\uD83C\uDF4E\uD83E\uDDC0\uD83C\uDF4E\uD83C\uDF50\uD83E\uDDC0\uD83C\uDF4E\uD83C\uDF50\uD83C\uDFD1\uD83C\uDFC2\uD83C\uDFB3\uD83C\uDFB3\uD83C\uDFD1\uD83C\uDFC2\uD83C\uDFB1\uD83C\uDF80\uD83C\uDF90\uD83C\uDF8D\uD83C\uDFAB\uD83C\uDF8D\uD83C\uDF91\uD83C\uDF91⛲\uD83C\uDFED\uD83C\uDFE9⛺\uD83C\uDFED\uD83D\uDD4B\uD83D\uDEBF\uD83D\uDECF\uD83D\uDEBF\uD83C\uDFB0\uD83D\uDECF\uD83D\uDC34\uD83D\uDC29\uD83D\uDC08\uD83D\uDC29\uD83D\uDC04\uD83D\uDC08\uD83C\uDF14\uD83C\uDF0D\uD83D\uDDFA\uD83C\uDF14\uD83D\uDCF2\uD83D\uDEB0\uD83D\uDEE2\uD83C\uDF11\uD83D\uDDFA\uD83C\uDF1A\uD83C\uDF13\uD83C\uDF26\uD83C\uDF08\uD83C\uDF2A\uD83C\uDF00☃\uD83C\uDF2A⛱\uD83D\uDD25\uD83C\uDF0A\uD83C\uDF0A\uD83D\uDCA7\uD83D\uDCF1\uD83D\uDD08\uD83D\uDCF2\uD83D\uDD0B\uD83D\uDD14";

        System.out.println(Strings.removeSpecialCharacters(text1));
        System.out.println(Strings.removeSpecialCharacters(text2));
        System.out.println(Strings.removeSpecialCharacters(text3));
        System.out.println(Strings.removeSpecialCharacters(text4));
        System.out.println("empty :" + Strings.removeSpecialCharacters(text5));

        Assert.assertEquals(" Mau ", Strings.removeSpecialCharacters(text1));
        Assert.assertEquals("123456", Strings.removeSpecialCharacters(text2));
        Assert.assertEquals("AbCd EFch", Strings.removeSpecialCharacters(text3));
        Assert.assertEquals("Cong hoa xa hoi chu nghia Viet Nam  Doc lap tu do hanh phuc", Strings.removeSpecialCharacters(text4));
        Assert.assertEquals("", Strings.removeSpecialCharacters(text5));
    }

    @Test
    public void testStripWhitespace() {
        String input = "+84 90 21672 33";
        String expected = "+84902167233";
        String actual = Strings.stripWhitespace(input);
        Assert.assertEquals(expected, actual);

        input = "+84 90 21672     33";
        expected = "+84902167233";
        actual = Strings.stripWhitespace(input);
        Assert.assertEquals(expected, actual);

        input = "  +84 90 21    \t672     33 ";
        expected = "+84902167233";
        actual = Strings.stripWhitespace(input);
        Assert.assertEquals(expected, actual);

        input = "  +84 90-21-672-33";
        expected = "+84902167233";
        actual = Strings.stripWhitespace(input);
        Assert.assertEquals(expected, actual);

        input = "  +84 90.21.672.33";
        expected = "+84902167233";
        actual = Strings.stripWhitespace(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNormalizeVietnamMobileNumber() {
        String input = "+84 90 21672 33";
        String expected = "0902167233";
        String actual = PhoneUtil.normalizeMobileNumber(input);
        Assert.assertEquals(expected, actual);

        input = "+84 090 21672 33";
        expected = "0902167233";
        actual = PhoneUtil.normalizeMobileNumber(input);
        Assert.assertEquals(expected, actual);

        input = "+84 123 510 5206";
        expected = "01235105206";
        actual = PhoneUtil.normalizeMobileNumber(input);
        Assert.assertEquals(expected, actual);
    }
}
