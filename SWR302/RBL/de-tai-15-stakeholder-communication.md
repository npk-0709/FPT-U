# Đề tài 15: Stakeholder Communication Challenges in Software Requirements Engineering
*(Bản cập nhật — đã bổ sung toàn bộ nguồn tìm được qua các lần trao đổi)*

## 1. Tổng quan đề tài

| RQ | Nội dung | Loại câu hỏi |
|---|---|---|
| RQ15.1 | Các thách thức giao tiếp phổ biến giữa stakeholder trong RE là gì? | Mô tả (descriptive) — cần taxonomy/phân loại |
| RQ15.2 | Các thách thức này ảnh hưởng thế nào đến chất lượng requirements và kết quả dự án? | Quan hệ nhân quả (causal) — cần bằng chứng thực nghiệm |
| RQ15.3 | Thực hành nào giúp cải thiện hiệu quả giao tiếp giữa stakeholder? | Giải pháp (prescriptive) — cần best practices |

---

## 2. Danh sách nguồn — xếp theo thứ tự ưu tiên sử dụng

### ⭐⭐⭐ Nhóm A — Nguồn lõi (Core), dùng làm khung chính cho cả báo cáo

**[A1] Damian, D. E., & Zowghi, D. (2003).**
*Requirements Engineering challenges in multi-site software development organizations.*
**Requirements Engineering Journal (Springer)**, Vol. 8, pp. 149–160.
- **Chất lượng:** Tạp chí chuyên biệt hàng đầu của ngành RE. Field study 7 tháng, 24 phỏng vấn bán cấu trúc tại 4 địa điểm, phương pháp grounded theory + Soft Systems Methodology.
- **Đóng góp:** Mô hình 3 tầng — *4 problems gốc* (Inadequate communication, Cultural diversity, Knowledge management, Time difference) → *8 challenges cụ thể* → *RE activities bị ảnh hưởng* (elicitation, prioritization, negotiation, validation, specification).
- **Phát hiện then chốt:** *"Inadequate communication" đóng góp vào 7/8 challenges* — giao tiếp kém là nguyên nhân gốc rễ chi phối gần như mọi vấn đề khác.
- **Dùng cho:** RQ15.1 (khung phân loại chính), RQ15.2 (mô hình tác động), RQ15.3 (Mục 6 "Recommendations for practice" có sẵn giải pháp cụ thể).
- **Hạn chế:** Single case study (1 tổ chức) — giới hạn external validity; dữ liệu công nghệ giao tiếp từ 2003 (email, teleconference) khác bối cảnh hiện tại.

**[A2] Iqbal, J., Ahmad, R., Nasir, M. H. N. B. M., & Noor, M. A. (2018).**
*A Framework to Address Communication Issues during Requirements Engineering Process for Software Development Outsourcing.*
**Journal of Internet Technology**, Vol. 19, No. 3, pp. 845–859.
- **Chất lượng:** Tạp chí index SCIE + Scopus (Đài Loan). Mixed-methods: literature review + khảo sát + phỏng vấn 12 practitioners (CATI) + đánh giá 2 vòng (3 chuyên gia học thuật dùng Cohen's Kappa κ=0.70–1.00; 60 practitioners công nghiệp, Prominence Level >50% cho cả 32 issues).
- **Đóng góp:** **32 communication issues** (27 từ literature + 5 từ khảo sát công nghiệp) map trực tiếp tới **28 RE practices** cụ thể, đã được validate thực nghiệm.
- **Dùng cho:** RQ15.1 (taxonomy chi tiết nhất), RQ15.2 (số liệu thất bại outsourcing: 50% hợp đồng không đạt kỳ vọng, 20-50% bị hủy), **RQ15.3 (nguồn mạnh nhất — 28 practice cụ thể, đã kiểm chứng)**.
- **Liên hệ:** Trích dẫn trực tiếp và mở rộng từ Damian & Zowghi [A1] — hai bài này dùng chung tạo thành mạch lý thuyết liền mạch.
- **Hạn chế:** Phạm vi hẹp hơn — tập trung bối cảnh Software Development Outsourcing (SDO), cần ghi rõ khi trích.

**[A3] Bjarnason, E., Wnuk, K., & Regnell, B. (2011).**
*Requirements are Slipping Through the Gaps — A Case Study on Causes & Effects of Communication Gaps in Large-Scale Software Development.*
19th IEEE International Requirements Engineering Conference (RE'11), pp. 37–46.
🔗 https://portal.research.lu.se/en/publications/requirements-are-slipping-through-the-gaps-a-case-study-on-causes/
- **Chất lượng:** IEEE RE Conference — hội nghị hàng đầu ngành RE.
- **Đóng góp:** Case study công ty ~5.000 nhân viên. Xác định 4 nguyên nhân: *scale, common views, temporal aspects, decision structures*.
- **Dùng cho:** RQ15.1, RQ15.2 (bằng chứng tác động lên chất lượng requirements ở quy mô lớn).

**[A4] Hidellaarachchi, I. M. D., Grundy, J., & Hoda, R. (2023).**
*The Influence of Human Aspects on Requirements Engineering-related Activities: Software Practitioners' Perspective.*
**ACM Transactions on Software Engineering and Methodology (TOSEM)**, Vol. 32, No. 5.
🔗 https://dl.acm.org/doi/10.1145/3546943
- **Chất lượng:** ACM TOSEM — tạp chí top-tier ngành SE.
- **Đóng góp:** Khảo sát thực tế với software practitioners; xác định communication skills là 1 trong những yếu tố con người quan trọng nhất ảnh hưởng RE; communication issues là thách thức phổ biến nhất.
- **Dùng cho:** RQ15.1 (góc nhìn "human aspects", bổ sung khía cạnh động lực/tính cách bên cạnh giao tiếp thuần túy), nguồn đối chiếu độ tin cậy cao cho các phát hiện từ A1-A3.

### ⭐⭐ Nhóm B — Nguồn bổ sung tốt, dùng minh họa/đối chiếu

**[B1] Al-Rawas, A., & Easterbrook, S.**
*Communication Problems in Requirements Engineering: A Field Study.*
🔗 https://www.academia.edu/6098486/COMMUNICATION_PROBLEMS_IN_REQUIREMENTS_ENGINEERING_A_FIELD_STUDY
- Bài kinh điển, nền tảng lý thuyết cho cả lĩnh vực (được [A1] trích dẫn). Dùng mở đầu phần Related Work để dẫn dắt lịch sử nghiên cứu.

**[B2] Coughlan, J., Lycett, M., & Macredie, R. D.**
*Communication issues in requirements elicitation: a content analysis of stakeholder experiences.*
Information and Software Technology (ScienceDirect).
- Khung phân tích giao tiếp 4 chiều — hữu ích làm góc nhìn bổ sung cho taxonomy ở RQ15.1.

**[B3] Nawaz, S., Mehmood, K., & Zai, A. (2025).**
*Challenges Faced by Stakeholders during the Requirement Engineering Phase: An Exploratory Study.*
International Journal of Innovations in Science & Technology (IJIST), Vol. 07, No. 02, pp. 1225–1234.
- Venue khu vực (Pakistan), không predatory nhưng cũng không top-tier. Phương pháp thực chất là **literature-based synthesis** (không phải khảo sát/phỏng vấn sơ cấp) dù tự gọi là "exploratory study" — **cần ghi chú rõ điều này khi trích**.
- Đóng góp: khung 3 nhóm (Personal/Professional/Environmental issues) + Bảng "State of the Art" tổng hợp 4 nghiên cứu (chính từ đây phát hiện ra nguồn A4).
- **Không trả lời RQ15.3** — tác giả tự nhận đây là hướng nghiên cứu tương lai.

**[B4] Damian, D. (2007).**
*Stakeholders in Global Requirements Engineering: Lessons Learned from Practice.*
IEEE Software, Vol. 24, No. 2, pp. 21–27.
- Cùng tác giả với [A1], mở rộng thêm về lessons learned thực tiễn — nên tìm đọc thêm để bổ sung.

### ⚠️ Nhóm C — Nguồn cần thận trọng, chỉ dùng làm bối cảnh phụ

**[C1] Otuneme, N. C., Eze, M., Kuyoro, S., & Ayankoya, F. (2023).**
*Bridging the Gap Between Stakeholder and Software Products: A Review of Software Requirement Engineering Techniques.*
ASRJETS, Vol. 93, No. 1, pp. 100–110.
- **Tạp chí ASRJETS đã xác nhận nằm trong danh sách predatory** (theo danh mục ĐH Duy Tân dựa trên Beall's List).
- Nội dung lệch trọng tâm (review chung về elicitation techniques, không tập trung communication).
- **Chỉ dùng** cho định nghĩa cơ bản (RE, FR/NFR) nếu thực sự cần, và luôn đối chiếu với nguồn uy tín hơn — không dùng làm luận điểm chính.

---

## 3. Từ khóa tìm kiếm bổ sung (nếu cần mở rộng thêm)

- `"communication gaps" "requirements engineering"` — đã dùng, hiệu quả cao
- `distributed requirements negotiation communication media` — hướng mở rộng từ Damian's other works
- `global software development communication taxonomy` — mở rộng phạm vi GSD
- `"lessons learned" global requirements engineering practice` — tìm thêm bài Damian (2007) [B4] dạng đầy đủ

---

## 4. Dàn ý báo cáo (cập nhật theo khung 32-issues/28-practices)

**I. Giới thiệu**
- Vai trò giao tiếp trong RE; số liệu về thất bại dự án outsourcing do giao tiếp kém (dẫn từ [A2])

**II. Phương pháp nghiên cứu**
- Literature review có cấu trúc; nêu rõ tiêu chí chọn nguồn (ưu tiên IEEE RE, REJ, ACM TOSEM, SCIE/Scopus-indexed journals)

**III. Kết quả theo từng RQ**

- **3.1 RQ15.1 — Taxonomy thách thức giao tiếp**
  - Dùng mô hình 4 problems → 8 challenges của [A1] làm khung chính
  - Đối chiếu/bổ sung bằng 32 issues chi tiết của [A2] (nhóm theo chủ đề: face-to-face vs. remote, trust, time zone, language, knowledge management...)
  - Bổ sung góc nhìn "human aspects" từ [A4] và khung 4 chiều từ [B2]
  - *Gợi ý trình bày:* 1 bảng tổng hợp — cột "Nguồn", cột "Loại thách thức", cột "Mô tả ngắn"

- **3.2 RQ15.2 — Ảnh hưởng đến chất lượng & kết quả dự án**
  - Bằng chứng định lượng: dự án trễ 6 tháng ([A1]), tỷ lệ thất bại outsourcing 20-50% ([A2])
  - Bằng chứng định tính: mất lòng tin, hiểu sai yêu cầu, xung đột kéo dài ([A1], [A3])

- **3.3 RQ15.3 — Thực hành cải thiện**
  - Dùng **28 RE practices** của [A2] làm khung chính (đã validate thực nghiệm — độ tin cậy cao)
  - Bổ sung khuyến nghị cụ thể từ Mục 6 của [A1]: họp kick-off trực tiếp, repository chung, kênh giao tiếp phi chính thức
  - *Gợi ý trình bày:* bảng map "Loại thách thức → Thực hành đề xuất → Nguồn"

**IV. Thảo luận**
- Khoảng trống nghiên cứu: hầu hết nguồn nền tảng (2003–2011) dựa trên công nghệ giao tiếp cũ (email, teleconference) — cần bàn về việc các thách thức có thay đổi ra sao với công cụ hiện đại (Slack, Zoom, AI-assisted communication)
- Nếu có mini-interview: đối chiếu dữ liệu thực tế với taxonomy từ literature

**V. Kết luận**

---

## 5. Cách trích dẫn (nhắc lại)

**IEEE style ví dụ:**
```
[1] D. E. Damian and D. Zowghi, "Requirements engineering challenges in
    multi-site software development organisations," Requirements Engineering,
    vol. 8, no. 3, pp. 149–160, 2003.

[2] J. Iqbal, R. Ahmad, M. H. N. B. Md Nasir, and M. A. Noor, "A framework
    to address communication issues during requirements engineering process
    for software development outsourcing," Journal of Internet Technology,
    vol. 19, no. 3, pp. 845–859, 2018.
```

**Lưu ý:** Luôn diễn giải lại (paraphrase) nội dung — không copy nguyên câu từ bài báo. Giữ chính xác thuật ngữ kỹ thuật gốc (ví dụ tên các "problems"/"challenges" của Damian & Zowghi) để không sai lệch ý nghĩa khi dịch/diễn giải.
