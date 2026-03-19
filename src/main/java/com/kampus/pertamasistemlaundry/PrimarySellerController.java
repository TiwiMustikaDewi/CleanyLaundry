package com.kampus.pertamasistemlaundry;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class PrimarySellerController implements Initializable {
    @FXML private AnchorPane panelDashboard;
    @FXML private AnchorPane panelManageOrders;
    // === panel layanan baru ===
    @FXML private AnchorPane panelManageServices;

    @FXML private Label lblTotalPesanan;
    @FXML private Label lblDiproses;
    @FXML private Label lblSiapAmbil;
    @FXML private Label lblSelesai;
    @FXML private Label lblTotalPendapatan;

    // ====== TABEL PESANAN ======
    @FXML private TableView<Pesanan> tvPesananAdmin;
    @FXML private TableColumn<Pesanan, String> colNoTransaksi;
    @FXML private TableColumn<Pesanan, String> colNamaPelanggan;
    @FXML private TableColumn<Pesanan, String> colTanggal;
    @FXML private TableColumn<Pesanan, String> colJenisLayanan;
    @FXML private TableColumn<Pesanan, Double> colBerat;
    @FXML private TableColumn<Pesanan, Double> colTotalHarga;
    @FXML private TableColumn<Pesanan, String> colStatus;

    // ====== TABEL LAYANAN (baru) ======
    @FXML private TableView<Layanan> tvLayanan;
    @FXML private TableColumn<Layanan, String> colNamaLayanan;
    @FXML private TableColumn<Layanan, Double> colHargaLayanan;

    // form layanan (kanan)
    @FXML private TextField tfNamaLayanan;
    @FXML private TextField tfHargaLayanan;
    @FXML private Button btnTambahLayanan;
    @FXML private Button btnEditLayanan;
    @FXML private Button btnHapusLayanan;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupDashboard();
        initializeTableViewPesanan();
        initializeManageServices();
    }

    // ================== NAVIGATION ==================
    @FXML
    private void switchToDashboard() {
        setPanelVisibility(panelDashboard);
        updateDashboard();
    }

    @FXML
    private void switchToManageOrders() {
        setPanelVisibility(panelManageOrders);
        loadPesananAdmin();
    }

    @FXML
    private void switchToManageServices() {
        setPanelVisibility(panelManageServices);
        loadLayanan();
    }

    @FXML
    private void handleLogout() throws Exception {
        App.adminSaatIni = null;
        App.setRoot("role_selection");
    }
    
    
    
    @FXML
    private TableColumn<Pesanan, Void> colAksi;

private void addButtonToTable() {
    Callback<TableColumn<Pesanan, Void>, TableCell<Pesanan, Void>> cellFactory = (TableColumn<Pesanan, Void> param) -> {
        return new TableCell<>() {

            private final Button btn = new Button("Hapus");

            {
                btn.setOnAction(event -> {
                    Pesanan p = getTableView().getItems().get(getIndex());
                    hapusPesanan(p);
                });
                btn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
    };

    colAksi.setCellFactory(cellFactory);
}


    private void setPanelVisibility(AnchorPane visiblePanel) {
        panelDashboard.setVisible(visiblePanel == panelDashboard);
        panelManageOrders.setVisible(visiblePanel == panelManageOrders);
        panelManageServices.setVisible(visiblePanel == panelManageServices);
    }

    // ================== DASHBOARD ==================
    private void setupDashboard() {
        setPanelVisibility(panelDashboard);
        updateDashboard();
    }

    private void updateDashboard() {
        try {
            int total = Database.countPesananStatus(null);
            int diproses = Database.countPesananStatus("Diproses");
            int siapAmbil = Database.countPesananStatus("Siap Ambil");
            int selesai = Database.countPesananStatus("Selesai");
            double revenue = Database.calculateTotalRevenue();
            lblTotalPesanan.setText(String.valueOf(total));
            lblDiproses.setText(String.valueOf(diproses));
            lblSiapAmbil.setText(String.valueOf(siapAmbil));
            lblSelesai.setText(String.valueOf(selesai));

            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            String formatPendapatan = formatRupiah.format(revenue).replace(",00", "");
            lblTotalPendapatan.setText(formatPendapatan);

        } catch (SQLException e) {
            showError("Gagal mengambil data dashboard.", e);
        }
    }

    // ================== TABEL PESANAN ==================
    private void initializeTableViewPesanan() {
        colNoTransaksi.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        colNamaPelanggan.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJenisLayanan.setCellValueFactory(new PropertyValueFactory<>("jenisLayanan"));

        colBerat.setCellValueFactory(new PropertyValueFactory<>("berat"));
        colBerat.setCellFactory(tc -> new TableCell<Pesanan, Double>() {
            @Override
            protected void updateItem(Double berat, boolean empty) {
                super.updateItem(berat, empty);
                setText(empty ? null : String.format("%.2f kg", berat));
            }
        });
        colTotalHarga.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));
        colTotalHarga.setCellFactory(tc -> new TableCell<Pesanan, Double>() {
            private final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            @Override
            protected void updateItem(Double harga, boolean empty) {
                super.updateItem(harga, empty);
                setText(empty ? null : formatRupiah.format(harga).replace(",00", ""));
            }
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
            "Diproses", "Siap Ambil", "Selesai", "Dibatalkan"
        );
        colStatus.setCellFactory(new Callback<TableColumn<Pesanan, String>, TableCell<Pesanan, String>>() {
            @Override
            public TableCell<Pesanan, String> call(TableColumn<Pesanan, String> param) {
                return new TableCell<Pesanan, String>() {
                    final ComboBox<String> statusCombo = new ComboBox<>(statusOptions);
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            statusCombo.setValue(item);
                            setGraphic(statusCombo);
                            statusCombo.setOnAction(event -> {
                                Pesanan pesanan = getTableView().getItems().get(getIndex());
                                String newStatus = statusCombo.getValue();
                                if (!newStatus.equals(pesanan.getStatus())) {
                                    handleStatusChange(pesanan, newStatus);
                                }
                            });
                        }
                    }
                };
            }
        });

        loadPesananAdmin();
    }

    private void handleStatusChange(Pesanan pesanan, String newStatus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Perubahan Status");
        alert.setHeaderText("Ubah status pesanan " + pesanan.getIdTransaksi() + " menjadi: " + newStatus + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Database.updateStatusPesanan(pesanan.getIdTransaksi(), newStatus);
                showInfo("Status pesanan " + pesanan.getIdTransaksi() + " berhasil diubah menjadi: " + newStatus);
                pesanan.setStatus(newStatus);
                tvPesananAdmin.refresh();
                updateDashboard();
            } catch (SQLException e) {
                showError("Gagal mengubah status pesanan.", e);
                loadPesananAdmin();
            }
        } else {
            loadPesananAdmin();
        }
    }

    private void loadPesananAdmin() {
        try {
            List<Pesanan> pesananList = Database.getAllPesananWithPelanggan();
            tvPesananAdmin.setItems(FXCollections.observableArrayList(pesananList));
        } catch (SQLException e) {
            showError("Gagal memuat data pesanan.", e);
        }
    }
    
    private void hapusPesanan(Pesanan p) {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Konfirmasi Hapus");
    confirm.setHeaderText("Hapus Pesanan?");
    confirm.setContentText("Apakah Anda yakin ingin menghapus pesanan dengan ID: " + p.getIdTransaksi());

    confirm.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            try {
                Database.deletePesanan(p.getIdTransaksi());
                loadPesananAdmin();
            } catch (Exception e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR, "Gagal menghapus pesanan!");
                error.show();
            }
        }
    });
}


    // ================== SHOW / ALERT HELPERS ==================
    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
    private void showWarning(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
    private void showError(String msg, Exception e) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(msg);
        a.setContentText(e == null ? msg : e.getMessage());
        a.showAndWait();
        if (e != null) e.printStackTrace();
    }
    

    // ================== MANAGE LAYANAN (BARU) ==================
    private void initializeManageServices() {
        // inisialisasi kolom
        colNamaLayanan.setCellValueFactory(new PropertyValueFactory<>("namaLayanan"));
        colHargaLayanan.setCellValueFactory(new PropertyValueFactory<>("hargaLayanan"));
        // format harga tampil
        colHargaLayanan.setCellFactory(tc -> new TableCell<Layanan, Double>() {
            private final NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            @Override
            protected void updateItem(Double harga, boolean empty) {
                super.updateItem(harga, empty);
                setText(empty || harga == null ? null : format.format(harga));
            }
        });

        // disable edit/hapus sampai ada selection
        btnEditLayanan.setDisable(true);
        btnHapusLayanan.setDisable(true);

        // selection listener: isi form saat baris dipilih
        tvLayanan.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                tfNamaLayanan.setText(newV.getNamaLayanan());
                tfHargaLayanan.setText(String.valueOf(newV.getHargaLayanan()));
                btnEditLayanan.setDisable(false);
                btnHapusLayanan.setDisable(false);
            } else {
                tfNamaLayanan.clear();
                tfHargaLayanan.clear();
                btnEditLayanan.setDisable(true);
                btnHapusLayanan.setDisable(true);
            }
        });

        // initial load (table kosong sampai panel dibuka)
        tvLayanan.setItems(FXCollections.observableArrayList());
    }

    private void loadLayanan() {
        try {
            List<Layanan> list = Database.getAllLayanan();
            tvLayanan.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Gagal memuat layanan.", e);
        }
    }

    @FXML
    private void handleTambahLayanan() {
        String nama = tfNamaLayanan.getText();
        String hargaTxt = tfHargaLayanan.getText();
        if (nama == null || nama.isBlank() || hargaTxt == null || hargaTxt.isBlank()) {
            showWarning("Isi nama dan harga layanan.");
            return;
        }
        try {
            double harga = Double.parseDouble(hargaTxt);
            Layanan l = new Layanan(nama, harga);
            Database.addLayanan(l);
            showInfo("Layanan berhasil ditambahkan.");
            tfNamaLayanan.clear();
            tfHargaLayanan.clear();
            loadLayanan();
        } catch (NumberFormatException nfe) {
            showWarning("Harga harus berupa angka.");
        } catch (SQLException e) {
            showError("Gagal menambah layanan.", e);
        }
    }

    @FXML
    private void handleEditLayanan() {
        Layanan sel = tvLayanan.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarning("Pilih layanan terlebih dahulu.");
            return;
        }
        String nama = tfNamaLayanan.getText();
        String hargaTxt = tfHargaLayanan.getText();
        if (nama == null || nama.isBlank() || hargaTxt == null || hargaTxt.isBlank()) {
            showWarning("Isi nama dan harga layanan.");
            return;
        }
        try {
            double harga = Double.parseDouble(hargaTxt);
            sel.setNamaLayanan(nama);
            sel.setHargaLayanan(harga);
            Database.updateLayanan(sel);
            showInfo("Layanan berhasil diperbarui.");
            loadLayanan();
        } catch (NumberFormatException nfe) {
            showWarning("Harga harus berupa angka.");
        } catch (SQLException e) {
            showError("Gagal memperbarui layanan.", e);
        }
    }

    @FXML
    private void handleHapusLayanan() {
        Layanan sel = tvLayanan.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarning("Pilih layanan yang ingin dihapus.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin hapus layanan \"" + sel.getNamaLayanan() + "\"?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                Database.deleteLayanan(sel.getIdLayanan());
                showInfo("Layanan dihapus.");
                tfNamaLayanan.clear();
                tfHargaLayanan.clear();
                loadLayanan();
            } catch (SQLException e) {
                showError("Gagal menghapus layanan.", e);
            }
        }
    }
    @FXML
public void handleDeleteOrder() {
    Pesanan selected = tvPesananAdmin.getSelectionModel().getSelectedItem();

    if (selected == null) {
        showAlert("Pilih pesanan yang ingin dihapus!");
        return;
    }

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setHeaderText("Yakin hapus pesanan?");
    confirm.setContentText("Transaksi: " + selected.getIdTransaksi());

    if (confirm.showAndWait().get() == ButtonType.OK) {
        try {
            Database.deletePesanan(selected.getIdTransaksi());
            loadPesananAdmin(); // refresh table
            showAlert("Pesanan berhasil dihapus.");
        } catch (SQLException e) {
            showAlert("Gagal menghapus pesanan: " + e.getMessage());
        }
    }
}
private void showAlert(String message) {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setHeaderText(null);
    a.setContentText(message);
    a.showAndWait();
}


}
