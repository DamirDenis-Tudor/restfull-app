import {Link} from "./hateoas.ts";

export const downloadFile = async (fileUrl: string) => {
    try {
        const response = await fetch(fileUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
            },
        });

        if (!response.ok) {
            if(response.status === 401) {
                sessionStorage.setItem('token', '');
                window.location.href = '/login'
            }
            throw new Error('Failed to fetch the file');
        }

        const blob = await response.blob();
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileUrl.split('/').pop()?.split("_category")[0] || 'downloaded-file';
        link.click();
    } catch (error) {
        console.error('Error downloading file:', error);
    }
};


export const uploadFile = async (fileUrl: Link, file: File) => {
    try {
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch(fileUrl.href, {
            method: fileUrl.type,
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
            },
            body: formData,
        });

        if (!response.ok) {
            if(response.status === 401) {
                sessionStorage.setItem('token', '');
                window.location.href = '/login'
            }
            throw new Error('Failed to upload file');
        }

    } catch (error) {
        console.error('Error uploading file:', error);
        throw error;
    }
};
