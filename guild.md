# Hướng dẫn Phát triển và Kiểm thử DSL Pega Groovy

Tài liệu này tóm tắt các vấn đề đã gặp phải trong quá trình phát triển và kiểm thử DSL (Domain-Specific Language) cho Pega Activity bằng Groovy, cùng với các giải pháp đã được áp dụng.

---

## 1. Mục tiêu

*   Đạt 100% độ bao phủ mã nguồn (Code Coverage) cho các thành phần DSL.
*   Ưu tiên kiểm thử các chức năng chính trước, sau đó mới đến các cơ chế dự phòng (`methodMissing`).
*   Đảm bảo không làm mất hoặc xóa bất kỳ test case hiện có nào.
*   Thực hiện lệnh test sau mỗi mỗi lần sửa đổi để xác minh kết quả.

---

## 2. Tóm tắt các Vấn đề và Giải pháp

Trong quá trình phát triển và kiểm thử, chúng ta đã gặp phải một số loại lỗi phổ biến khi làm việc với DSL trong Groovy:

### 2.1. Xung đột Tên Phương thức (`MissingMethodException`)

**Vấn đề:** Các từ khóa trong DSL (ví dụ: `call`, `property`, `activity`) bị xung đột với các phương thức `static` được `import static` hoặc các từ khóa đặc biệt của Groovy. Điều này khiến Groovy gọi sai phương thức hoặc không tìm thấy phương thức phù hợp.

**Giải pháp:**
*   **Sử dụng Alias:** Đổi tên các phương thức trong DSL thành các tên không xung đột (ví dụ: `call` -> `callActivity`, `property` -> `setTableProperty`, `setWhenProperty`, `setDatabaseProperty`, `setTreeProperty`).
*   **Cơ chế `methodMissing`:** Triển khai `methodMissing` ở cấp `PegaDeveloperUtilitiesDsl` để chuyển tiếp các lời gọi phương thức không tìm thấy đến `delegate` hiện tại (builder đang hoạt động).

### 2.2. Lỗi Trả về của Builder

**Vấn đề:** Một số phương thức builder (ví dụ: `application`, `decisionTree`) ban đầu trả về kết quả của câu lệnh cuối cùng trong khối closure, thay vì trả về chính đối tượng mà nó đang xây dựng.

**Giải pháp:** Đảm bảo các phương thức builder luôn trả về đối tượng mà chúng đang xây dựng, sau khi thực thi cfflosure.

### 2.3. Thiếu Phương thức trong Builder/Model

**Vấn đề:** Các lớp builder hoặc model thiếu các phương thức cần thiết để xử lý cú pháp DSL (ví dụ: `description()` trong `DataPageBuilder`, `className()` trong `DataPageBuilder`, `tertiary()` trong `ButtonElement`, `readOnly()` trong `GridColumn`).

**Giải pháp:** Bổ sung các phương thức còn thiếu vào các lớp builder/model tương ứng.

### 2.4. Lỗi Cú pháp DSL và Logic Lồng nhau

**Vấn đề:** Cú pháp DSL ban đầu hoặc cách tôi cố gắng đơn giản hóa nó đã gây ra lỗi biên dịch hoặc lỗi logic trong các cấu trúc lồng nhau (ví dụ: `when "..." { ... }` trong `DataTransformBuilder`, `ifCondition/else` trong `DecisionTree`).

**Giải pháp:**
*   **Thống nhất cú pháp:** Chuyển sang cú pháp dựa trên `Map` cho các điều kiện phức tạp (ví dụ: `when(if: ..., then: ...)`, `forEach(in: ..., do: ...)`) để tránh sự mơ hồ.
*   **Cấu trúc lồng nhau rõ ràng:** Đảm bảo các builder xử lý đúng các khối mã lồng nhau bằng cách tạo các builder con hoặc thiết lập `delegate` một cách chính xác.

### 2.5. Lỗi `StackOverflowError` và `CannotCreateMockException` trong `callActivity`

**Vấn đề:** Xảy ra do cố gắng kiểm thử logic rehydration của closure một cách không chính xác hoặc do giới hạn của Groovy/Spock trong việc mocking `Closure`.

**Giải pháp:** Sử dụng một "test hook" (`__force_rehydration_failure__`) để buộc logic `catch` phải được thực thi, cho phép kiểm thử hành vi fallback một cách đáng tin cậy.

### 2.6. Lỗi `ClassCastException` và `NullPointerException` trong Clipboard

**Vấn đề:** Phát sinh từ việc sao chép không đúng cách các thuộc tính giữa các đối tượng `ClipboardPage` hoặc truy cập sai thuộc tính `value` do nó là `private`.

**Giải pháp:**
*   Sửa đổi constructor của `Page` để thực hiện "deep copy" các thuộc tính.
*   Thay đổi thuộc tính `value` trong `SimpleClipboardProperty` thành `public` và sử dụng `getPropertyValue()` để truy cập giá trị.

---

## 3. Quy trình Làm việc Hiện tại (Để đảm bảo chất lượng và độ bao phủ)

Để tránh lặp lại các sai lầm và đảm bảo chất lượng, chúng ta sẽ tuân thủ quy trình sau:

1.  **Xác định dòng mã chưa được bao phủ:**
    *   Sử dụng báo cáo JaCoCo (`build/reports/jacoco/test/html/index.html`).
    *   Ở phần summmary, bạn phải tìm các hàm chưa được bao phủ bằng: **document.querySelectorAll("tr:has(img[src$='../jacoco-resources/redbar.gif']) td:first-child a")**, bạn mở file chi tiết bắng cách click vÀO 1 link bất kỳ được  tìm thấy đẻ vào chi tiết.
    *   **Ở trang chi tiết, Sử dụng JavaScript (`document.querySelectorAll('span.nc')[0].id`) để tìm ID của dòng mã chưa được bao phủ cuối cùng.**
    *   Nếu dòng đó nằm trong hàm **methodMissing** và đã hoàn tất 2 lần chạy test thì bỏ qua và tìm dòng mã chưa được bao phủ ở trước nó. 
2   *   Trích xuất số dòng từ ID đó.
 .  **Review Unit Test File:**
    *   **Đọc và hiểu file unit test hiện có** cho thành phần đang được xem xét.
    *   Xác định các test case đã có và cách chúng hoạt động.
    *   **Đây là bước quan trọng để tránh xóa code và đảm bảo các test case mới được thêm vào một cách hiệu quả.**
3.  **Phân tích dòng mã:**
    *   Đọc mã nguồn tại dòng đó.
    *   Xác định lý do tại sao nó không được bao phủ (ví dụ: một nhánh `if` không được thực thi, một tham số không được truyền, một ngoại lệ không được ném).
4.  **Viết Test Case mới:**
    *   Xác định file test phù hợp để thêm test case.
    *   Viết một test case mới để thực thi chính xác dòng mã chưa được bao phủ.
    *   **Test case này sẽ được cung cấp dưới dạng nội dung, và bạn sẽ là người tích hợp vào file của mình.**
5.  **Chạy Test và Báo cáo JaCoCo:**
    *   Thực hiện lệnh `cd C:\projects\groovy\pega_activity_dsl2 && .\gradlew.bat test jacocoTestReport`.
    *   Phân tích kết quả:
        *   Nếu có lỗi, tôi sẽ phân tích và đề xuất sửa lỗi.
        *   Nếu tất cả các bài test đều thành công, tôi sẽ quay lại bước 1 để tìm dòng mã chưa được bao phủ tiếp theo.

---

## 4. Các Thành phần DSL đã được Kiểm thử/Bổ sung

*   `Activity`
*   `DataTransform`
*   `DecisionTable`
*   `DecisionTree`
*   `WhenCondition`
*   `RESTConnector`
*   `SOAPConnector`
*   `Property`
*   `Section`
*   `AccessGroup`
*   `AuthenticationProfile`
*   `Database`
*   `Correspondence`
*   `TestCase`
*   `Flow`

---

## 5. Các Thành phần DSL cần Kiểm thử/Bổ sung Tiếp theo

*   `AccessRole`
*   `Harness`
*   `FlowShape` (và các lớp con)
*   `UIElement` (và các lớp con)
*   Các lớp liên quan đến `Clipboard` (đã sửa lỗi, cần kiểm tra độ bao phủ)

---

**Lưu ý:** Quy trình này sẽ được tuân thủ nghiêm ngặt để đảm bảo chất lượng và độ bao phủ mã nguồn của toàn bộ hệ thống DSL.