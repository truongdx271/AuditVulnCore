
import com.viettel.audit.def.BaselineHead;
import com.viettel.audit.def.BaselineSub;
import com.viettel.audit.def.ItemObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.viettel.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author longnt39
 */
public class ForDebugModule {

    public static void main(String[] args) {
        //Người lập trình gán giá trị đường dẫn tới thư mục chứ source code cần test
//        String projectPath = "E:\\SOURCE\\Tool ANM V2.0\\WebApp";
        String projectPath = "D:\\WebAppTest";
//        String projectPath = "E:\\BinhHH6\\Audit\\VTN\\NPMS_ThiTruong\\SRC_NPMS_ThiTruong\\Source IBM\\PNMS_WEB_TT\\PNMS_Web";

        //Đường dẫn tới file cấu hình cho module
        String defineFilePath = "src\\main\\java\\define.xml";

        if (!loadModuleScan(defineFilePath)) {
            System.out.println("File định nghĩa cho module bị lỗi");
            return;
        }

        //Tạo đối tượng từ Class chính trong file định nghĩa:
        JavaBaselineWebApp javaBaselineWebApp = new JavaBaselineWebApp();

        try {
            //Gọi Method chính để thực thi. Tên method này mặc định là: "process" + "Tên Class chính"
            javaBaselineWebApp.processJavaBaselineWebApp(projectPath);
        } catch (IOException ex) {
            Logger.getLogger(ForDebugModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Lấy kết quả xác thực dữ liệu đầu vào
        if (!javaBaselineWebApp.getValidateResult()) {
            //Nếu có lỗi, lấy thông điệp lỗi
            System.out.println("Dữ liệu đầu vào lỗi: " + javaBaselineWebApp.getValidateMessage());
            return;
        }

        List<BaselineHead> listBaselineHead = new ArrayList<>();

        //Gọi method lấy dữ liệu đầu ra
        listBaselineHead = javaBaselineWebApp.getListBaselineHead();

        //In kết quả đầu ra
        for (BaselineHead baselineHead : listBaselineHead) {
            System.out.println("Baseline head: " + baselineHead.getIdentify() + "." + baselineHead.getDisplayTxt());

            List<BaselineSub> listBaselineSub = baselineHead.getListBaselineSub();
            for (BaselineSub baselineSub : listBaselineSub) {
                System.out.println("Baseline sub: " + baselineSub.getIdentify() + "." + baselineSub.getDisplayTxt());

                List<ItemObject> listItemObjects = baselineSub.getListItemObject();

                for (ItemObject tempItem : listItemObjects) {
                    // In đối tượng trong 1 cell thành 1 dòng
                    System.out.println("Item: " + tempItem.getDisplayTxt() + " | " + tempItem.getDisplayPath() + " | " + tempItem.getLineNumber() + " | " + tempItem.getResult());
                }

            }

        }

    }

    public static boolean loadModuleScan(String defineFilePath) {

        //declare array of module property
        String[] attModule = new String[8];

        File defineFile = new File(defineFilePath);

        //Read XML file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //disable dtd valiation in XML
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            Document doc = dBuilder.parse(defineFile);

            int countTag = 2;

            //Đọc thông tin định nghĩa menu tìm kiếm
            NodeList listDefineNode = doc.getElementsByTagName("define");
            for (int i = 0; i < listDefineNode.getLength(); i++) {
                Node tempNode = listDefineNode.item(i);
                //get text tag
                String valueName = tempNode.getAttributes().getNamedItem("name").getNodeValue().toLowerCase();
                if (valueName.contains("language")) {
                    attModule[2] = tempNode.getTextContent();
                    countTag++;
                }

            }

            if (countTag != 3) {
                return false;
            }

            //Đọc thông tin định nghĩa các class và method chính
            NodeList listClassNode = doc.getElementsByTagName("class");
            for (int i = 0; i < listClassNode.getLength(); i++) {
                Node tempNode = listClassNode.item(i);
                //get value of tag
                String valueClass = tempNode.getAttributes().getNamedItem("name").getNodeValue().toLowerCase();
                if (valueClass.contains("source")) {
                    attModule[3] = tempNode.getTextContent();
                    countTag++;
                } else if (valueClass.contains("main")) {
                    attModule[4] = tempNode.getTextContent();
                    countTag++;
                }
            }

            if (countTag != 5) {
                return false;
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }
}
