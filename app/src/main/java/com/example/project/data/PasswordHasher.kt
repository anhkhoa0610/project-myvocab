// File: com.example.project.data.PasswordHasher.kt

package com.example.project.data

import org.mindrot.jbcrypt.BCrypt // BẠN CẦN THÊM THƯ VIỆN NÀY

object PasswordHasher {

    // --- Cần thêm dependency BCrypt vào build.gradle (module) ---
    // implementation 'org.mindrot:jbcrypt:0.4'
    // Hoặc phiên bản mới nhất nếu có

    private const val LOG_ROUNDS = 12 // Số vòng lặp: Càng cao càng an toàn nhưng càng chậm

    /**
     * Hash (mã hóa) mật khẩu. Luôn tạo ra một hash khác nhau cho cùng một mật khẩu
     * nhờ salt ngẫu nhiên.
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS))
    }

    /**
     * Xác minh mật khẩu nhập vào có khớp với hash đã lưu trong database hay không.
     */
    fun verifyPassword(password: String, hash: String): Boolean {
        // Hàm checkpw của BCrypt đã tự động xử lý salt và hash
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            // Xử lý lỗi nếu hash không hợp lệ (ví dụ: hash null hoặc lỗi định dạng)
            false
        }
    }
}