import {request} from "../utils/service";
export function login(data: { email: string; password: string; }) {
    return request({
        url: "/login",
        method: "post",
        data
    })
}

export function registry(data: { email: string; password: string; }) {
    return request({
        url: "/registry",
        method: "post",
        data
    })
}

export function forgotPassword(data: { email: string; }) {
    return request({
        url: "/chat",
        method: "post",
        data
    })
}