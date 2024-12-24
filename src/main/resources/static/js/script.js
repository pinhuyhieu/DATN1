$(function () {

    // Thêm các phương thức kiểm tra tùy chỉnh
    jQuery.validator.addMethod('lettersonly', function (value, element) {
        return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
    }, 'Chỉ cho phép chữ cái và dấu cách hợp lệ');

    jQuery.validator.addMethod('space', function (value, element) {
        return /^[^\s].*[^\s]$/.test(value); // Không khoảng trắng đầu/cuối
    }, 'Không được chứa khoảng trắng ở đầu hoặc cuối');

    jQuery.validator.addMethod('numericOnly', function (value, element) {
        return /^[0-9]+$/.test(value);
    }, 'Chỉ được nhập số');

    jQuery.validator.addMethod('positiveNumber', function (value, element) {
        return /^[1-9]\d*(\.\d+)?$/.test(value);
    }, 'Giá trị phải là số dương hợp lệ');

    jQuery.validator.addMethod('notEqual', function (value, element, param) {
        return value !== param;
    }, 'Vui lòng chọn một tùy chọn hợp lệ');

    jQuery.validator.addMethod('pincode', function (value, element) {
        return /^[0-9]{6}$/.test(value);
    }, 'Mã bưu điện phải chứa 6 chữ số');

    // Quy tắc và thông báo xác thực chung
    function getValidationRules() {
        return {
            rules: {
                name: { required: true, lettersonly: true },
                email: { required: true, email: true, space: true },
                mobileNumber: { required: true, numericOnly: true, minlength: 10, maxlength: 12, space: true },
                password: { required: true, space: true },
                confirmPassword: { required: true, equalTo: '#password', space: true },
                address: { required: true },
                city: { required: true, space: true },
                state: { required: true },
                pincode: { required: true, pincode: true, space: true },
                file: { required: true, extension: "jpg|jpeg|png" }
            },
            messages: {
                name: { required: 'Vui lòng nhập tên', lettersonly: 'Tên không hợp lệ' },
                email: { required: 'Vui lòng nhập email', email: 'Email không hợp lệ', space: 'Không được chứa khoảng trắng' },
                mobileNumber: {
                    required: 'Vui lòng nhập số điện thoại',
                    numericOnly: 'Số điện thoại không hợp lệ',
                    minlength: 'Số điện thoại phải có ít nhất 10 chữ số',
                    maxlength: 'Số điện thoại không được vượt quá 12 chữ số',
                    space: 'Không được chứa khoảng trắng'
                },
                password: { required: 'Vui lòng nhập mật khẩu', space: 'Không được chứa khoảng trắng' },
                confirmPassword: { required: 'Vui lòng nhập lại mật khẩu', equalTo: 'Mật khẩu không khớp', space: 'Không được chứa khoảng trắng' },
                address: { required: 'Vui lòng nhập địa chỉ' },
                city: { required: 'Vui lòng nhập thành phố', space: 'Không được chứa khoảng trắng' },
                state: { required: 'Vui lòng chọn tỉnh/thành phố' },
                pincode: { required: 'Vui lòng nhập mã bưu điện', pincode: 'Mã bưu điện không hợp lệ', space: 'Không được chứa khoảng trắng' },
                file: { required: 'Vui lòng tải lên hình ảnh', extension: 'Chỉ chấp nhận định dạng jpg, jpeg, hoặc png' }
            },
            errorElement: "div",
            errorClass: "text-danger",
            highlight: function (element) {
                $(element).addClass("is-invalid");
            },
            unhighlight: function (element) {
                $(element).removeClass("is-invalid").addClass("is-valid");
            }
        };
    }

    // Áp dụng quy tắc xác thực cho từng biểu mẫu
    $("#userRegister").validate(getValidationRules());
    $("#orders").validate(getValidationRules());
    $("#resetPassword").validate(getValidationRules());

    // Xác thực biểu mẫu Add Category
    $("#addCategoryForm").validate({
        rules: {
            name: { required: true, lettersonly: true },
            isActive: { required: true },
            file: { required: true, extension: "jpg|jpeg|png" }
        },
        messages: {
            name: { required: 'Tên danh mục là bắt buộc', lettersonly: 'Tên danh mục không hợp lệ' },
            isActive: { required: 'Vui lòng chọn trạng thái' },
            file: { required: 'Hình ảnh là bắt buộc', extension: 'Chỉ chấp nhận định dạng jpg, jpeg, hoặc png' }
        },
        errorElement: "div",
        errorClass: "text-danger",
        highlight: function (element) {
            $(element).addClass("is-invalid");
        },
        unhighlight: function (element) {
            $(element).removeClass("is-invalid").addClass("is-valid");
        }
    });

    // Xác thực biểu mẫu Add Product
    $("#addProductForm").validate({
        rules: {
            title: { required: true, minlength: 3, maxlength: 100 },
            description: { required: true, minlength: 5 },
            category: { required: true, notEqual: "--select--" },
            price: { required: true, positiveNumber: true },
            stock: { required: true, digits: true },
            file: { required: true, extension: "jpg|jpeg|png" }
        },
        messages: {
            title: { required: 'Vui lòng nhập tiêu đề sản phẩm', minlength: 'Tiêu đề phải có ít nhất 3 ký tự', maxlength: 'Tiêu đề không được vượt quá 100 ký tự' },
            description: { required: 'Vui lòng nhập mô tả sản phẩm', minlength: 'Mô tả phải có ít nhất 5 ký tự' },
            category: { required: 'Vui lòng chọn danh mục sản phẩm', notEqual: 'Vui lòng chọn danh mục hợp lệ' },
            price: { required: 'Vui lòng nhập giá sản phẩm', positiveNumber: 'Giá sản phẩm phải là số dương hợp lệ' },
            stock: { required: 'Vui lòng nhập số lượng tồn kho', digits: 'Số lượng tồn kho chỉ chứa số nguyên dương' },
            file: { required: 'Vui lòng tải lên hình ảnh sản phẩm', extension: 'Chỉ chấp nhận định dạng ảnh jpg, jpeg, hoặc png' }
        },
        errorElement: "div",
        errorClass: "text-danger",
        highlight: function (element) {
            $(element).addClass("is-invalid");
        },
        unhighlight: function (element) {
            $(element).removeClass("is-invalid").addClass("is-valid");
        }
    });

});
