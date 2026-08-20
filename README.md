# Arclights

Thể loại: Thủ thành (tower-defense) viết bằng Java + JavaFX, lấy cảm hứng từ Arknights: người chơi triển khai các **Operator** (Vanguard, Guard, Defender, Sniper, Caster, Medic, Supporter, Specialist) lên bản đồ dạng lưới để chặn và tiêu diệt các đợt quái (**Enemy**) trước khi chúng đến điểm đích
Video demo: https://youtu.be/z0lKXZYDRsI

## Tính năng chính

- **8 lớp Operator** với stats, tầm đánh, loại sát thương (vật lý/phép) và skills (SP(skill point) tự hồi hoặc hồi khi tấn công)
- **4 loại quái** (Originium Slug, Soldier, Hound, Big Bob) với tốc độ, giáp, kháng phép khác nhau
- **Nhiều stage** với map, số làn đường và wave riêng
- **Shop & tiến trình**: dùng crystal thu được để mua Operator mới, lưu filesave vào `~/.arclights/save.properties`
- **Âm thanh, animation sprite** cho từng state (idle/walk/attack/death) của Operator và Enemy

## Yêu cầu hệ thống

- JDK 17 trở lên

## Cách chạy phần mềm đã build

**Cách 1 - chạy file cài đặt/app image:** mở thư mục `target/dist/Arclights`, chạy file thực thi `Arclights` (Windows: `Arclights.exe`).

**Cách 2 - chạy fat-jar bằng dòng lệnh:**
```bash
java -jar arclights.jar
```

## Build từ source

Yêu cầu: JDK 17+, Maven 3.8+.

```bash
# 1. Clone project
git clone https://github.com/Syrex310/Arclights
cd arclights

# 2. Chạy thử trực tiếp
mvn javafx:run

# 3. Đóng gói thành fat-jar
mvn clean package
# -> tạo target/arclights.jar

## Cấu trúc thư mục

```
src/main/java/com/arclights/
├── App.java              # Điểm vào chính, quản lý chuyển scene và game loop
├── Launcher.java         # Điểm vào cho bản build (fat-jar)
├── animation/            # Sprite, animation state, preload asset
├── audio/                # SoundManager
├── entity/
│   ├── operator/         # 8 lớp Operator + hệ thống skill
│   └── enemy/            # Enemy, EnemyType
├── handlers/              # InputController - xử lý input người chơi
├── managers/              # DeploymentManager, EnemyManager - logic gameplay
├── models/                 # GameMap, MapConfig, OperatorCatalog, PlayerProgress, wave/
└── ui/                     # StartMenu, ShopMenu, MapRenderer, các panel UI khác
```

## Bản quyền tài nguyên (asset credits)

Toàn bộ hình ảnh nhân vật/portrait, sprite, background và một số hiệu ứng UI trong dự án được lấy từ game **Arknights** (bản quyền thuộc **Hypergryph / Studio Montagne**, phát hành bởi **Yostar**). Đây là dự án học thuật, phi thương mại, chỉ nhằm mục đích minh hoạ kỹ thuật lập trình game trong khuôn khổ bài tập lớn môn học, không dùng để phân phối hay thu lợi nhuận. Toàn bộ quyền sở hữu trí tuệ đối với các asset gốc thuộc về chủ sở hữu bản quyền tương ứng
