package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PracticeFormPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // 1. Khai báo các Locator
    private By firstNameInput = By.id("firstName");
    private By lastNameInput = By.id("lastName");
    private By emailInput = By.id("userEmail");
    private By mobileInput = By.id("userNumber");
    
    // Locator cho Date Picker
    private By dobInput = By.id("dateOfBirthInput");
    private By monthSelect = By.className("react-datepicker__month-select");
    private By yearSelect = By.className("react-datepicker__year-select");
    
    // Locator cho Subject, Upload File, Address, Submit
    private By subjectsInput = By.id("subjectsInput");
    private By uploadPictureInput = By.id("uploadPicture");
    private By currentAddressInput = By.id("currentAddress");
    private By submitBtn = By.id("submit");
    
    // Locator cho Modal xác nhận
    private By modalTitle = By.id("example-modal-sizes-title-lg");
    private By modalBody = By.className("modal-body");
    private By closeModalBtn = By.id("closeLargeModal");

    public PracticeFormPage(WebDriver driver) {
        this.driver = driver;
        // Khởi tạo Explicit Wait 10 giây
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- YÊU CẦU 2: Điền thông tin cá nhân ---
    public void enterPersonalInfo(String firstName, String lastName, String email, String gender, String mobile) {
        // Explicit Wait vị trí 1: Chờ ô FirstName hiển thị
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput)).sendKeys(firstName);
        driver.findElement(lastNameInput).sendKeys(lastName);
        driver.findElement(emailInput).sendKeys(email);
        
        // Chọn giới tính (Male / Female / Other) bằng XPath dựa trên text
        WebElement genderRadio = driver.findElement(By.xpath("//label[text()='" + gender + "']"));
        scrollToElement(genderRadio);
        genderRadio.click();

        driver.findElement(mobileInput).sendKeys(mobile);

        System.out.println("--- Đã nhập thông tin cá nhân ---");
        System.out.println("Họ tên: " + firstName + " " + lastName + " | Email: " + email + " | SĐT: " + mobile);
    }

    // --- YÊU CẦU 3: Chọn ngày sinh bằng Date Picker ---
    public String selectDateOfBirth(String day, String month, String year) {
        WebElement dobElement = driver.findElement(dobInput);
        scrollToElement(dobElement);
        dobElement.click();

        // Chọn Tháng và Năm bằng thẻ Select
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(monthSelect))).selectByVisibleText(month);
        new Select(driver.findElement(yearSelect)).selectByVisibleText(year);

        // Chọn Ngày (loại trừ những ngày bị mờ thuộc tháng trước/sau bằng not(contains(@class, 'outside-month')))
        String dayXpath = String.format("//div[contains(@class,'react-datepicker__day') and not(contains(@class,'outside-month')) and text()='%s']", day);
        driver.findElement(By.xpath(dayXpath)).click();

        return driver.findElement(dobInput).getAttribute("value");
    }

    // Chọn môn học và sở thích
    public void enterSubjectAndHobby(String subject, String hobby, String address) {
        WebElement subElement = driver.findElement(subjectsInput);
        scrollToElement(subElement);
        subElement.sendKeys(subject);
        subElement.sendKeys(Keys.ENTER);

        WebElement hobbyCheckbox = driver.findElement(By.xpath("//label[text()='" + hobby + "']"));
        scrollToElement(hobbyCheckbox);
        hobbyCheckbox.click();

        driver.findElement(currentAddressInput).sendKeys(address);
    }

    // --- YÊU CẦU 4: Tải tệp lên form ---
    public String uploadFile(String absoluteFilePath) {
        WebElement uploadElement = driver.findElement(uploadPictureInput);
        uploadElement.sendKeys(absoluteFilePath);
        // Trả về giá trị của ô input sau khi upload (thường có dạng C:\fakepath\ten_file.png)
        return uploadElement.getAttribute("value");
    }

    // --- YÊU CẦU 5: Nhấn Submit và lấy thông tin Modal ---
    public void clickSubmit() {
        WebElement btn = driver.findElement(submitBtn);
        scrollToElement(btn);
        // Dùng JS Click để tránh bị banner quảng cáo của DemoQA che khuất
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean isConfirmationModalDisplayed() {
        // Explicit Wait vị trí 2: Chờ Modal xác nhận hiện lên sau khi Submit
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle)).isDisplayed();
    }

    public String getModalContent() {
        return driver.findElement(modalBody).getText();
    }

    // Hàm hỗ trợ cuộn trang tới phần tử
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}