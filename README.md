# Arclights

Thể loại: Thủ thành (tower-defense) viết bằng Java + JavaFX, lấy cảm hứng từ Arknights: người chơi triển khai các **Operator** (Vanguard, Guard, Defender, Sniper, Caster, Medic, Supporter, Specialist) lên bản đồ dạng lưới để chặn và tiêu diệt các đợt quái (**Enemy**) trước khi chúng đến điểm đích

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
git clone <link-repo-github-cua-ban>
cd arclights

# 2. Chạy thử trực tiếp (không cần đóng gói)
mvn javafx:run

# 3. Đóng gói thành fat-jar
mvn clean package
# -> sinh ra target/arclights.jar

# 4. (tuỳ chọn) Tạo file cài đặt/app image native
mvn jpackage:jpackage
# -> sinh ra target/dist/Arclights (hoặc .exe/.dmg/.deb tuỳ hệ điều hành)
```

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

## Hướng dẫn cài đặt

1. Tải file build tương ứng với hệ điều hành
2. Giải nén (nếu là app image) và chạy file thực thi/chạy `java -jar arclights.jar` nếu dùng jar
