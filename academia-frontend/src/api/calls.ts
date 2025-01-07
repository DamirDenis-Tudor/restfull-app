export const downloadFile = async (fileUrl: string) => {
    try {
        const response = await fetch(fileUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
            },
        });

        if (!response.ok) {
            throw new Error('Failed to fetch the file');
        }

        const blob = await response.blob();
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileUrl.split('/').pop() || 'downloaded-file';
        link.click();
    } catch (error) {
        console.error('Error downloading file:', error);
    }
};