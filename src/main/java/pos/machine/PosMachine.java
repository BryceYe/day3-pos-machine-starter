package pos.machine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static pos.machine.ItemsLoader.loadAllItems;

public class PosMachine {

    public String printReceipt(List<String> barcodes) {
        List<Item> items = loadAllItems();
        if(checkBarcodeIfExist(barcodes, items)){
            List<Receipt> receipts = buildReceipt(groupByBarcode(barcodes), items);
            return convertToString(receipts);
        } else {
            return "Error: Invalid barcode found in the input.";
        }
    }

    public List<Receipt> buildReceipt(Map<String, Integer> barcodeMap, List<Item> items) {
        List<Receipt> receipts = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : barcodeMap.entrySet()) {
            String barcode = entry.getKey();
            int quantity = entry.getValue();
            Item item = findItemByBarcode(barcode, items);
            if (item != null) {
                int unitPrice = item.getPrice();
                int subtotal = unitPrice * quantity;
                Receipt receipt = new Receipt(item.getName(), quantity, unitPrice, subtotal);
                receipts.add(receipt);
            }
        }
        return receipts;
    }

    public boolean checkBarcodeIfExist(List<String> barcodes, List<Item> items) {
        for (String barcode : barcodes) {
            boolean found = false;
            for (Item item : items) {
                if (item.getBarcode().equals(barcode)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Integer> groupByBarcode(List<String> barcodes) {
        Map<String, Integer> barcodeMap = new LinkedHashMap<>(); // 使用 LinkedHashMap 保证插入顺序
        for (String barcode : barcodes) {
            barcodeMap.put(barcode, barcodeMap.getOrDefault(barcode, 0) + 1);
        }
        return barcodeMap;
    }

    public String convertToString(List<Receipt> receipts) {
        StringBuilder receiptString = new StringBuilder();
        receiptString.append("***<store earning no money>Receipt***\n");
        int total = 0;
        for (Receipt receipt : receipts) {
            receiptString.append(String.format("Name: %s, Quantity: %d, Unit price: %d (yuan), Subtotal: %d (yuan)\n",
                    receipt.getName(), receipt.getQuantity(), receipt.getUnitPrice(), receipt.getSubtotal()));
            total += receipt.getSubtotal();
        }
        receiptString.append("----------------------\n");
        receiptString.append(String.format("Total: %d (yuan)\n", total));
        receiptString.append("**********************");
        return receiptString.toString();
    }

    public Item findItemByBarcode(String barcode, List<Item> items) {
        for (Item item : items) {
            if (item.getBarcode().equals(barcode)) {
                return item;
            }
        }
        return null;
    }
}
