package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolInfoUtil {

    public static List getAllKindSchoolListBySchoolName(String schoolName){
        List resultList = new ArrayList();
//        resultList.addAll(getElemListBySchoolName(schoolName));
//        resultList.addAll(getMiddListBySchoolName(schoolName));
        resultList.addAll(getHighListBySchoolName(schoolName));
        resultList.addAll(getUnivListBySchoolName(schoolName));
        resultList.addAll(getSeetListBySchoolName(schoolName));
        resultList.addAll(getAlteListBySchoolName(schoolName));
        return resultList;
    }
    public static List getElemListBySchoolName(String schoolName){ return getSchoolList("elem_list",schoolName); }
    public static List getMiddListBySchoolName(String schoolName){ return getSchoolList("midd_list",schoolName); }
    public static List getHighListBySchoolName(String schoolName){ return getSchoolList("high_list",schoolName); }
    public static List getUnivListBySchoolName(String schoolName){ return getSchoolList("univ_list",schoolName); }
    //특수/기타학교
    public static List getSeetListBySchoolName(String schoolName){ return getSchoolList("seet_list",schoolName); }
    //대안학교
    public static List getAlteListBySchoolName(String schoolName){ return getSchoolList("alte_list",schoolName); }

    private static List getSchoolList(String kindOfSchool , String schoolName){
        List resultList = new ArrayList();
        try {
            String searchParam = "";
            if (schoolName != null && !schoolName.isEmpty()){
                searchParam = "&searchSchulNm=" + URLEncoder.encode(schoolName, "utf-8");
            }
            String url = "http://www.career.go.kr/cnet/openapi/getOpenApi.xml?apiKey=a4bcbad73bcfddbdd2c59347bea982da&svcType=api&svcCode=SCHOOL&gubun="+kindOfSchool+"&thisPage=1&perPage=10000"+searchParam;

            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document doc = dBuilder.parse(url);

            NodeList nList = doc.getElementsByTagName("content");

            for(int temp = 0; temp < nList.getLength(); temp++){
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){

                    Element eElement = (Element) nNode;
                    Map schoolInfoMap = new HashMap();
                    schoolInfoMap.put("name" , getTagValue("schoolName", eElement));
                    schoolInfoMap.put("schoolType" , kindOfSchool.substring(0 , kindOfSchool.length()-5));
                    schoolInfoMap.put("link" , getTagValue("link", eElement));
                    schoolInfoMap.put("adres" , getTagValue("adres", eElement));
                    schoolInfoMap.put("region" , getTagValue("region", eElement));
                    schoolInfoMap.put("seq" , getTagValue("seq", eElement));
                    resultList.add(schoolInfoMap);
                }	// for end
            }	// if end

        }catch (ParserConfigurationException | SAXException | IOException e){
            e.printStackTrace();
        }
        return resultList;
    }

    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }

}
