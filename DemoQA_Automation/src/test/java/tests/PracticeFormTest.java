package tests;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.PracticeFormPage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PracticeFormTest {
    private WebDriver driver;
    private PracticeFormPage formPage;
    private final String URL = "https://demoqa.com/automation-practice-form";

    @BeforeMethod
    public void setUp() {
        // YÊU CẦU 1a, 1b: Mở trình duyệt Chrome và truy cập trang
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
        formPage = new PracticeFormPage(driver);
    }

    // --- YÊU CẦU 7: Tạo ít nhất 3 bộ dữ liệu đầu vào khác nhau ---
    @DataProvider(name = "studentData")
    public Object[][] getStudentData() {
        return new Object[][]{
        	{"Nguyen", "Trong", "nguyentrong14062005@gmail.com", "Male", "0901234567", "15", "May", "2005", "Maths", "Sports", "615 AU CO, HCM"},
            {"Tran", "Binh", "binh.tran@gmail.com", "Female", "0912345678", "20", "August", "2002", "English", "Reading", "624 AU CO, HCM"},
            {"Le", "Cuong", "cuong.le@gmail.com", "Male", "0987654321", "10", "December", "2004", "Physics", "Music", "615 AU CO, HCM"}
        };
    }

    // --- YÊU CẦU 8: Tự động hóa quy trình hoàn chỉnh ---
    @Test(dataProvider = "studentData")
    public void testSubmitPracticeForm(String firstName, String lastName, String email, String gender,
                                       String mobile, String day, String month, String year,
                                       String subject, String hobby, String address) throws IOException {
        
        System.out.println("\n==========================================");
        System.out.println("ĐANG CHẠY TEST VỚI DỮ LIỆU: " + firstName + " " + lastName);

        // 1. Kiểm tra URL và Tiêu đề trang (Yêu cầu 1c, 1d)
        Assert.assertEquals(driver.getCurrentUrl(), URL, "URL không khớp!");
        Assert.assertFalse(driver.getTitle().isEmpty(), "Tiêu đề trang bị rỗng!");
        System.out.println("Tiêu đề trang: " + driver.getTitle());

        // 2. Điền thông tin cá nhân (Yêu cầu 2)
        formPage.enterPersonalInfo(firstName, lastName, email, gender, mobile);

        // 3. Chọn ngày sinh và kiểm tra định dạng hiển thị (Yêu cầu 3)
        String displayedDob = formPage.selectDateOfBirth(day, month, year);
        System.out.println("Ngày sinh sau khi chọn: " + displayedDob);
        // Kiểm tra định dạng trả về kiểu "15 May 2003" (3 ký tự đầu của tháng)
        String expectedMonthShort = month.substring(0, 3);
        Assert.assertTrue(displayedDob.contains(day) && displayedDob.contains(expectedMonthShort) && displayedDob.contains(year),
                "Định dạng ngày sinh không đúng!");

        // Điền thêm môn học, sở thích, địa chỉ
        formPage.enterSubjectAndHobby(subject, hobby, address);

        // 4. Tải tệp lên form và kiểm tra tên file (Yêu cầu 4)
        File fileToUpload = new File("src/test/resources/avatar.png");
        String uploadedValue = formPage.uploadFile(fileToUpload.getAbsolutePath());
        System.out.println("Đường dẫn file trên form: " + uploadedValue);
        Assert.assertTrue(uploadedValue.contains("avatar.png"), "Tên file tải lên không đúng!");

        // Chụp màn hình thời điểm 1: TRƯỚC SUBMIT (Yêu cầu 8)
        captureScreen("BeforeSubmit_" + firstName);

        // 5. Nhấn Submit và kiểm tra Modal xác nhận (Yêu cầu 5)
        formPage.clickSubmit();
        Assert.assertTrue(formPage.isConfirmationModalDisplayed(), "Modal xác nhận không xuất hiện!");

        // Chụp màn hình thời điểm 2: SAU SUBMIT (Yêu cầu 8)
        captureScreen("AfterSubmit_" + firstName);

        // Kiểm tra nội dung xác nhận và in ra Console
        String modalText = formPage.getModalContent();
        System.out.println("--- THÔNG TIN XÁC NHẬN SAU KHI SUBMIT ---");
        System.out.println(modalText);

        Assert.assertTrue(modalText.contains(firstName + " " + lastName), "Sai họ tên trong modal!");
        Assert.assertTrue(modalText.contains(email), "Sai email trong modal!");
        Assert.assertTrue(modalText.contains(mobile), "Sai SĐT trong modal!");
        Assert.assertTrue(modalText.contains("avatar.png"), "Sai tên file trong modal!");
    }

    // Hàm chụp màn hình theo đúng mẫu code của đề bài
    public void captureScreen(String prefix) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File dest = new File("screenshots/" + prefix + "_" + time + ".png");
        FileUtils.copyFile(src, dest);
        System.out.println("Đã lưu ảnh chụp màn hình: " + dest.getPath());
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        // Nếu test bị lỗi (FAIL), tự động chụp màn hình lỗi
        if (ITestResult.FAILURE == result.getStatus()) {
            captureScreen("ERROR_" + result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }
}