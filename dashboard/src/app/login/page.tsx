"use client"

import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { toast, ToastContainer } from 'react-toastify'
import { useRouter } from 'next/navigation'
import { Label } from '@/components/ui/label'
import InputWithTitle from '@/components/ui/input/inputWithTitle'
import { login } from '../../lib/api/auth'
import iAuthResult from '../../model/iAuthResult'
import { Button } from '@/components/ui/button'
import { Util } from '@/util/globle'
export interface iLoginData {
    name: string;
    password: string;
}

const Login = () => {
    const rout = useRouter()
    const [data, setData] = useState<iLoginData>({
        name:
            //'',
            'ali535@gmail.com',
        password:
            //'',
            '12AS@#fs'
    });

    const loginFun = useMutation({
        mutationFn: (data: iLoginData) => login({ name: data.name, password: data.password }),
        onError: (e) => {
            toast.error(e.message)
        },
        onSuccess: (result) => {
            const resultData = result.data as iAuthResult;
            Util.token = resultData.refreshToken;
            rout.push("/dashboard");
        }
    })

    return (
        <div className="h-screen w-full flex justify-center items-center bg-gradient-to-br from-[#f5f7fa] to-[#c3cfe2] dark:from-[#18181b] dark:to-[#23272f]">
            <div className='w-full max-w-md px-6 py-10 bg-white dark:bg-[#23272f] rounded-2xl shadow-xl border border-gray-200 dark:border-gray-700 flex flex-col items-center mb-10 sm:mb-20'>
                <Label className='text-4xl sm:text-5xl font-bold text-[#452CE8] dark:text-[#a5b4fc]'>Sign In</Label>
                <div className='h-10 sm:h-20' />

                <InputWithTitle
                    title='Email'
                    name={data.name}
                    placeHolder='Enter Your Email'
                    onchange={(value: string) => { setData((data) => ({ ...data, name: value })) }}
                />
                <div className='h-3 sm:h-5' />
                <InputWithTitle
                    title='Password'
                    name={data.password}
                    placeHolder='Enter Your Password'
                    onchange={(value: string) => { setData((data) => ({ ...data, password: value })) }}
                />
                <div className='h-3 sm:h-4' />
                <Button
                    disabled={
                        data.name.trim().length < 1 ||
                        data.password.trim().length < 1 ||
                        loginFun.isPending
                    }
                    className='w-full mt-2 py-3 bg-[#452CE8] hover:bg-[#3721b6] text-white font-semibold rounded-lg transition-colors duration-200 shadow-md disabled:opacity-60 disabled:cursor-not-allowed dark:bg-[#6366f1] dark:hover:bg-[#4f46e5]'
                    onClick={() => loginFun.mutate(data)}
                >
                    {loginFun.isPending ? 'Signing In...' : 'Sign In'}
                </Button>
            </div>
            <ToastContainer />
        </div>
    )
}

export default Login;