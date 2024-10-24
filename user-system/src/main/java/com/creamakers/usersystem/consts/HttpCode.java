package com.creamakers.usersystem.consts;

/**
 * 定义常见的 HTTP 状态码（String 类型）。
 * HTTP 状态码分为 5 类：100-199 是信息响应，200-299 是成功响应，300-399 是重定向响应，
 * 400-499 是客户端错误，500-599 是服务器错误。
 *
 * 通常在 Web API 中使用这些状态码来表示不同的请求处理结果。
 */
public class HttpCode {

    // 1xx Informational 信息响应

    /**
     * 100 Continue
     * 服务器已收到请求头，客户端应继续发送请求主体。
     */
    public static final String CONTINUE = "100";

    /**
     * 101 Switching Protocols
     * 服务器同意客户端请求的协议切换。
     */
    public static final String SWITCHING_PROTOCOLS = "101";

    /**
     * 102 Processing (WebDAV)
     * 服务器已收到并正在处理请求，但尚未提供响应。
     */
    public static final String PROCESSING = "102";

    // 2xx Success 成功响应

    /**
     * 200 OK
     * 请求成功，服务器已成功处理并返回所请求的资源。
     * 常见于GET、POST请求。
     */
    public static final String OK = "200";

    /**
     * 201 Created
     * 请求成功并创建了新的资源，通常在 POST 请求之后返回。
     * 例如用户注册成功后，返回此状态码。
     */
    public static final String CREATED = "201";

    /**
     * 202 Accepted
     * 服务器已接受请求，但尚未处理。请求可能会被处理，或者被拒绝。
     */
    public static final String ACCEPTED = "202";

    /**
     * 204 No Content
     * 服务器成功处理了请求，但没有返回内容。通常在DELETE请求后使用。
     */
    public static final String NO_CONTENT = "204";

    // 3xx Redirection 重定向响应

    /**
     * 301 Moved Permanently
     * 资源已永久移动到新位置，后续请求应使用新的 URL。
     * 常用于 URL 重构或网站迁移场景。
     */
    public static final String MOVED_PERMANENTLY = "301";

    /**
     * 302 Found
     * 临时重定向，资源暂时位于另一个 URL，客户端应继续使用原始 URL 进行后续请求。
     * 一般用于用户登录跳转等场景。
     */
    public static final String FOUND = "302";

    /**
     * 304 Not Modified
     * 资源未修改，可以使用缓存的版本。通常用于 GET 请求的缓存机制。
     * 可以提高性能，避免重复获取相同的数据。
     */
    public static final String NOT_MODIFIED = "304";

    // 4xx Client Error 客户端错误

    /**
     * 400 Bad Request
     * 服务器无法理解请求，由于客户端错误（例如语法错误或无效参数）。
     * 开发中常用于请求数据格式不对时返回。
     */
    public static final String BAD_REQUEST = "400";

    /**
     * 401 Unauthorized
     * 客户端未提供身份验证凭据或身份验证失败。
     * 常用于需要用户登录的 API 接口。
     */
    public static final String UNAUTHORIZED = "401";

    /**
     * 403 Forbidden
     * 服务器理解请求，但拒绝授权访问。客户端没有权限执行操作。
     * 通常用于访问控制和权限限制。
     */
    public static final String FORBIDDEN = "403";

    /**
     * 404 Not Found
     * 服务器找不到请求的资源。此状态码表示 URL 无效或资源已删除。
     * **建议：404 不要频繁使用，可以通过提供有意义的错误信息或默认页面来提升用户体验。**
     */
    public static final String NOT_FOUND = "404";

    /**
     * 405 Method Not Allowed
     * 请求使用的 HTTP 方法不被允许。通常用于 REST API 中指定请求方式（如 GET 或 POST）错误的情况。
     */
    public static final String METHOD_NOT_ALLOWED = "405";


    /**
     * 409 Conflict
     * 请求冲突，通常发生在资源的状态与请求的不兼容。
     * 在用户注册过程中，如果用户名或电子邮件已经存在，可以返回该状态码。
     */
    public static final String CONFLICT = "409";


    /**
     * 429 Too Many Requests
     * 客户端在给定的时间内发送了太多请求。通常用于限流。
     */
    public static final String TOO_MANY_REQUESTS = "429";

    // 5xx Server Error 服务器错误

    /**
     * 500 Internal Server Error
     * 服务器内部发生错误，无法完成请求。通常用于捕获未处理的异常。
     * **建议：在生产环境中返回 500 时，提供通用错误信息，并记录详细日志用于排查问题。**
     */
    public static final String INTERNAL_SERVER_ERROR = "500";

    /**
     * 502 Bad Gateway
     * 服务器作为网关或代理，从上游服务器接收到无效响应。
     * 一般用于反向代理服务器与后端服务之间的连接错误。
     */
    public static final String BAD_GATEWAY = "502";

    /**
     * 503 Service Unavailable
     * 服务器暂时无法处理请求，通常是由于服务器过载或维护。
     * 在服务负载过大或系统维护时可以返回此状态码，并在响应中告知预计恢复时间。
     */
    public static final String SERVICE_UNAVAILABLE = "503";

    /**
     * 504 Gateway Timeout
     * 服务器作为网关或代理，未能及时从上游服务器接收到请求。
     * 用于处理服务之间的超时问题。
     */
    public static final String GATEWAY_TIMEOUT = "504";

    /**
     * 505 HTTP Version Not Supported
     * 服务器不支持请求中使用的 HTTP 版本。
     */
    public static final String HTTP_VERSION_NOT_SUPPORTED = "505";
}
