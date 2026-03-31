package app.callcheck.mobile.data.search.provider;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a8\u0006\u0004"}, d2 = {"createOkHttpClient", "Lokhttp3/OkHttpClient;", "timeoutSeconds", "", "search_debug"})
public final class HttpClientFactoryKt {
    
    /**
     * 온디바이스 HTTP 클라이언트 팩토리.
     *
     * 모든 검색 요청은 디바이스에서 직접 수행한다.
     * 중앙 서버, 프록시, 외부 API 없음.
     */
    @org.jetbrains.annotations.NotNull()
    public static final okhttp3.OkHttpClient createOkHttpClient(long timeoutSeconds) {
        return null;
    }
}