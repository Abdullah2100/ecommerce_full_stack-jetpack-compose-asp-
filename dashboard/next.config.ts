import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  eslint: {
    ignoreDuringBuilds: true,
  },
  images: { domains: ['localhost'] },
  allowedDevOrigins: [
    '192.168.1.45', 
    'localhost', 
    '0.0.0.0', 
   '72.60.232.89'
  ],
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://72.60.232.89:5077/api/:path*',
      },
    ];
  },
  env: {
    NEXT_PUBLIC_PASE_URL: '',
  },
};

export default nextConfig;
