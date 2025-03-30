import axios, { type AxiosInstance, type AxiosRequestConfig } from "axios"
import { message } from 'ant-design-vue';
import { get } from "lodash-es"

/** 创建请求实例 */
function createService(): AxiosInstance {
  // 创建一个 axios 实例
  const service = axios.create()
  // 请求拦截
  service.interceptors.request.use(
    (config) => config,
    // 发送失败
    (error) => Promise.reject(error)
  )
  // 响应拦截（可根据具体业务作出相应的调整）
  service.interceptors.response.use(
    (response) => {
      console.log(response.config.url);
      
      let msg = ""
      const res = response.data

      const respType = response.config.responseType
      if (respType === "blob") {
        return res
      }
      if (!res) {
        msg = "无响应值"
        message.error(msg)
        return Promise.reject(msg)
      }
      if (response.config.url?.startsWith("/login") || res.code == "200") {
        return res
      }
      msg = res.message || "请求错误"
      message.error({
        content: msg,
        duration: 2
      })
      return Promise.reject(msg)
    },
    (error) => {
      // status 是 HTTP 状态码
      const status = get(error, "response.status")
      switch (status) {
        case 400:
          error.message = "请求错误"
          break
        case 401:
          error.message = "未授权，请登录"
          break
        case 403:
          location.reload()
          break
        case 404:
          error.message = "请求地址出错"
          break
        case 408:
          error.message = "请求超时"
          break
        case 500:
          error.message = "服务器内部错误"
          break
        case 501:
          error.message = "服务未实现"
          break
        case 502:
          error.message = "网关错误"
          break
        case 503:
          error.message = "服务不可用"
          break
        case 504:
          error.message = "网关超时"
          break
        case 505:
          error.message = "HTTP版本不受支持"
          break
        default:
          break
      }
      message.error({
        content: error.message,
        duration: 2
      })
      return Promise.reject(error)
    }
  )
  return service
}

/** 创建请求方法 */
function createRequestFunction(service: AxiosInstance) {
  return function (config: AxiosRequestConfig) {
    const configDefault = {
      timeout: 500000,
      baseURL: '/api',
      data: {}
    }
    return service(Object.assign(configDefault, config))
  }
}

/** 用于网络请求的实例 */
export const service = createService()
/** 用于网络请求的方法 */
export const request = createRequestFunction(service)
