package com.kampus.pertamasistemlaundry;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent; 
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox; 
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class PrimaryController implements Initializable {

    @FXML private Label lblSelamatDatang; 
    
    @FXML private AnchorPane panelDashboard;
    @FXML private AnchorPane panelPesananBaru;
    @FXML private AnchorPane panelStatusPesanan; 
    @FXML private AnchorPane panelProfil; 

    @FXML private Label lblPesananAktif;
    @FXML private Label lblTotalSelesai;
    @FXML private TableView<Pesanan> tvPesananTerakhir;
    @FXML private TableColumn<Pesanan, String> colIdTransaksi;
    @FXML private TableColumn<Pesanan, String> colTanggal;
    @FXML private TableColumn<Pesanan, String> colLayanan;
    @FXML private TableColumn<Pesanan, Double> colTotalHarga;
    @FXML private TableColumn<Pesanan, String> colStatus;
    
    @FXML private ChoiceBox<String> cbLayanan;
    @FXML private TextField tfBerat;
    @FXML private Label lblHargaPerKg;
    @FXML private Label lblTotalBiaya;
    
    @FXML private TableView<Pesanan> tvPesananAktif;
    @FXML private TableColumn<Pesanan, String> colStatusIdTransaksi;
    @FXML private TableColumn<Pesanan, String> colStatusTanggal;
    @FXML private TableColumn<Pesanan, String> colStatusLayanan;
    @FXML private TableColumn<Pesanan, Double> colStatusTotalHarga;
    @FXML private TableColumn<Pesanan, String> colStatusStatus;
    @FXML private TableColumn<Pesanan, Void> colAksi; 

    @FXML private Label lblNama;
    @FXML private Label lblUsername;
    @FXML private Label lblAlamat;
    @FXML private Label lblNoTelepon;

    private Map<String, Double> hargaLayananMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showPanel(panelDashboard);
        
        if (App.pelangganSaatIni != null) {
            lblSelamatDatang.setText("Selamat Datang, " + App.pelangganSaatIni.getNama() + "!");
        }
        
        loadLayananToChoiceBox();
        setupTableDashboard();
        setupTablePesananAktif();
        updateDashboard();
        updateProfilData();
        
        cbLayanan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateHargaPerKg();
            calculateTotalBiaya();
        });
        tfBerat.textProperty().addListener((obs, oldVal, newVal) -> {
            calculateTotalBiaya();
        });
    }
    
    @FXML
    private void handleMenuClick(ActionEvent event) {
        Button source = (Button) event.getSource();
        String buttonId = source.getId();

        showPanel(null);
        
        switch (buttonId) {
            case "btnDashboard":
                showPanel(panelDashboard);
                updateDashboard();
                break;
            case "btnPesan":
                showPanel(panelPesananBaru);
                loadLayananToChoiceBox();
                tfBerat.clear();
                lblTotalBiaya.setText("Rp 0");
                break;
            case "btnStatus":
                showPanel(panelStatusPesanan);
                updateTabelPesananAktif();
                break;
            case "btnProfil":
                showPanel(panelProfil);
                updateProfilData();
                break;
            default:
                showPanel(panelDashboard);
                updateDashboard();
                break;
        }
    }

    private void showPanel(AnchorPane panelToShow) {
        panelDashboard.setVisible(false);
        panelPesananBaru.setVisible(false);
        panelStatusPesanan.setVisible(false);
        if (panelProfil != null) panelProfil.setVisible(false); 
        
        if (panelToShow != null) {
            panelToShow.setVisible(true);
        }
    }
    
    private void setupTableDashboard() {
        colIdTransaksi.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colLayanan.setCellValueFactory(new PropertyValueFactory<>("jenisLayanan"));
        colTotalHarga.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        colTotalHarga.setCellFactory(tc -> new TableCell<Pesanan, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                    setText(formatter.format(price).replace("Rp", "Rp ").replace(",00", ""));
                }
            }
        });
    }

    private void updateDashboard() {
        if (App.pelangganSaatIni == null) return;
        
        int idPelanggan = App.pelangganSaatIni.getId();
        
        try {
            int aktif = Database.getTotalPesananAktif(idPelanggan);
            int selesai = Database.getCountPesananByStatus("Selesai", idPelanggan);
            lblPesananAktif.setText(String.valueOf(aktif));
            lblTotalSelesai.setText(String.valueOf(selesai));
            
            List<Pesanan> pesananList = Database.getPesananByPelanggan(idPelanggan);
            int limit = Math.min(pesananList.size(), 5);
            tvPesananTerakhir.setItems(FXCollections.observableArrayList(pesananList.subList(0, limit)));
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal memuat data Dashboard:\\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadLayananToChoiceBox() {
        try {
            List<Layanan> layananList = Database.getAllLayanan();
            cbLayanan.getItems().clear();
            hargaLayananMap.clear();
            
            for (Layanan layanan : layananList) {
                cbLayanan.getItems().add(layanan.getNamaLayanan());
                hargaLayananMap.put(layanan.getNamaLayanan(), layanan.getHargaLayanan());
            }
            if (!cbLayanan.getItems().isEmpty()) {
                cbLayanan.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal memuat daftar layanan:\\n" + e.getMessage());
        }
    }
    
    private void updateHargaPerKg() {
        String selectedLayanan = cbLayanan.getSelectionModel().getSelectedItem();
        if (selectedLayanan != null && hargaLayananMap.containsKey(selectedLayanan)) {
            double harga = hargaLayananMap.get(selectedLayanan);
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            lblHargaPerKg.setText("Harga/Kg: " + formatter.format(harga).replace("Rp", "Rp ").replace(",00", ""));
        } else {
            lblHargaPerKg.setText("Harga/Kg: -");
        }
    }
    
    private void calculateTotalBiaya() {
        String selectedLayanan = cbLayanan.getSelectionModel().getSelectedItem();
        String beratText = tfBerat.getText().trim();
        double totalHarga = 0.0;
        
        try {
            double berat = Double.parseDouble(beratText);
            if (selectedLayanan != null && hargaLayananMap.containsKey(selectedLayanan)) {
                double hargaPerKg = hargaLayananMap.get(selectedLayanan);
                totalHarga = berat * hargaPerKg;
            }
        } catch (NumberFormatException e) {
        }
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        lblTotalBiaya.setText(formatter.format(totalHarga).replace("Rp", "Rp ").replace(",00", ""));
    }
    
    @FXML
    private void handlePesan() {
        String selectedLayanan = cbLayanan.getSelectionModel().getSelectedItem();
        String beratText = tfBerat.getText().trim();
        
        if (selectedLayanan == null || beratText.isEmpty() || App.pelangganSaatIni == null) {
            showAlert(Alert.AlertType.WARNING, "Input Kurang", "Harap pilih Layanan dan masukkan Berat.");
            return;
        }
        
        try {
            double berat = Double.parseDouble(beratText);
            if (berat <= 0) {
                 showAlert(Alert.AlertType.WARNING, "Input Berat Tidak Valid", "Berat harus lebih dari 0.");
                 return;
            }
            double hargaPerKg = hargaLayananMap.get(selectedLayanan);
            double totalHarga = berat * hargaPerKg;
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Pesanan");
            alert.setHeaderText("Konfirmasi Detail Pesanan Anda:");
            alert.setContentText("Layanan: " + selectedLayanan 
                               + "\nBerat: " + berat + " Kg"
                               + "\nTotal Biaya: " + lblTotalBiaya.getText()
                               + "\n\nLanjutkan dengan pemesanan ini?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // =============== POPUP PEMBAYARAN QRIS ===================
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Pembayaran QRIS");
                
                popupStage.setOnCloseRequest(event -> {
                    event.consume(); // cegah window tertutup
                    Alert alertClose = new Alert(Alert.AlertType.WARNING);
                    alertClose.setTitle("Peringatan");
                    alertClose.setHeaderText(null);
                    alertClose.setContentText("Lakukan pembayaran terlebih dahulu!");
                    alertClose.showAndWait();
});

                // Layout popup
                VBox box = new VBox(15);
                box.setPadding(new Insets(20));
                box.setStyle("-fx-background-color: #d8e7ff; -fx-alignment: center;");

                // Judul
                Label labelInfo = new Label("Silakan lakukan pembayaran QRIS:");
                labelInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e4468;");

                // Gambar QR (pastikan file ada di resources!)
                ImageView imgQR = new ImageView(new Image(getClass().getResourceAsStream("/images/qris.png")));
                imgQR.setFitWidth(200);
                imgQR.setPreserveRatio(true);

                // Tombol lanjut
                   Button btnSelesai = new Button("Saya Sudah Bayar");
                   btnSelesai.setStyle("-fx-background-color: #2e4468; -fx-text-fill: white; -fx-font-weight: bold;");
                   btnSelesai.setOnAction(e -> popupStage.close());

                // Masukkan semua ke layout
                box.getChildren().addAll(labelInfo, imgQR, btnSelesai);

                // Tampilkan popup
                Scene scene = new Scene(box, 320, 380);
                popupStage.setScene(scene);
                popupStage.showAndWait();

                String idTransaksi = generateIdTransaksi();
                String tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                Pesanan pesananBaru = new Pesanan(idTransaksi, tanggal, selectedLayanan, berat, totalHarga, "Diproses");
                
                Database.addPesanan(pesananBaru);
                
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Pesanan berhasil dibuat!\nID Transaksi: " + idTransaksi);
                
                showPanel(panelStatusPesanan);
                updateTabelPesananAktif();
            }
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Berat harus berupa angka.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal menyimpan pesanan:\\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupTablePesananAktif() {
        colStatusIdTransaksi.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        colStatusTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colStatusLayanan.setCellValueFactory(new PropertyValueFactory<>("jenisLayanan"));
        colStatusTotalHarga.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));
        colStatusStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        colStatusTotalHarga.setCellFactory(tc -> new TableCell<Pesanan, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                    setText(formatter.format(price).replace("Rp", "Rp ").replace(",00", ""));
                }
            }
        });
        
        colAksi.setCellFactory(col -> new TableCell<Pesanan, Void>() {
            private final Button btn = new Button("Batalkan");
            {
                btn.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction((ActionEvent event) -> {
                    Pesanan pesanan = getTableView().getItems().get(getIndex());
                    handleBatalPesanan(pesanan);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Pesanan pesanan = getTableView().getItems().get(getIndex());
                    if ("Diproses".equals(pesanan.getStatus())) {
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void updateTabelPesananAktif() {
        if (App.pelangganSaatIni == null) return;
        
        try {
            List<Pesanan> pesananList = Database.getPesananByPelanggan(App.pelangganSaatIni.getId());
            tvPesananAktif.setItems(FXCollections.observableArrayList(pesananList));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal memuat daftar pesanan aktif:\\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleBatalPesanan(Pesanan pesanan) {
        if (!"Diproses".equals(pesanan.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Tidak Dapat Dibatalkan", "Pesanan hanya dapat dibatalkan jika statusnya 'Diproses'.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Pembatalan");
        alert.setHeaderText("Anda yakin ingin membatalkan pesanan " + pesanan.getIdTransaksi() + "?");
        alert.setContentText("Pesanan yang dibatalkan tidak dapat dikembalikan.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Database.updateStatusPesanan(pesanan.getIdTransaksi(), "Dibatalkan");
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Pesanan " + pesanan.getIdTransaksi() + " berhasil dibatalkan.");
                updateTabelPesananAktif(); 
                updateDashboard(); 
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal membatalkan pesanan:\\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void updateProfilData() {
        if (App.pelangganSaatIni != null) {
            lblNama.setText(App.pelangganSaatIni.getNama());
            lblUsername.setText(App.pelangganSaatIni.getUsername());
            lblAlamat.setText(App.pelangganSaatIni.getAlamat());
            lblNoTelepon.setText(App.pelangganSaatIni.getNoTelepon());
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText(null);
        alert.setContentText("Anda yakin ingin keluar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            App.pelangganSaatIni = null; 
            App.setRoot("role_selection"); 
        }
    }

    private String generateIdTransaksi() {
        String tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); 
        return "TR-" + tanggal + "-" + randomNumber;
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}