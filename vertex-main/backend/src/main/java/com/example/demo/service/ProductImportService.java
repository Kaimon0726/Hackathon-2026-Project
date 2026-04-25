package com.example.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class ProductImportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JSONファイルから製品データを読み込みます。
     * @param fileName resources/json 配下のファイル名
     * @return 読み込まれた製品データのリスト（Map形式）
     */
    public List<Map<String, Object>> loadProductsFromJson(String fileName) {
        String path = "/json/" + fileName;
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("ファイルが見つかりません: " + path);
            }
            return objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("JSONの読み込みに失敗しました: " + fileName, e);
        }
    }

    /**
     * インポートしたデータをDBに保存するロジック（スケルトン）
     * 実際の保存には外部で定義される Repository/Entity が必要です。
     */
    public void importToDatabase(String fileName) {
        List<Map<String, Object>> productData = loadProductsFromJson(fileName);
        // TODO: Repositoryを使用した保存処理
        // 本来は productRepository.saveAll(...) を呼び出すが、
        // 現状は service パッケージ外の依存関係を最小限にするためロジックのみ定義
    }
}
