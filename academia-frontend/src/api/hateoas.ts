export interface Link {
    href: string;
    type?: string;
}

export interface Lecture {
    id: string;
    lectureName: string;
    studyYear: number;
    lectureType: string;
    categoryType: string;
    examinationType: string;
    _links: Record<string, Link>;
}

export interface AssessmentTest {
    type: string;
    weight: number;
}

export interface FileMetadata {
    file_name: string;
    category: string;
    uploaded_at: string;
    size: number;
}

export interface FileInfo {
    file_metadata: FileMetadata;
    _links: Record<string, Link>;
}

export interface Professor {
    professorId: number;
    affiliation: string;
    associationType: string;
    email: string;
    firstName: string;
    graderType: string;
    lastName: string;
    _links: Record<string, Link>
}

export interface Student {
    id: number;
    firstName: string;
    lastName: string;
    cycleType: string;
    email: string;
    studyYear: string;
    studentGroup: string;
    _links: Record<string, Link>
}

export interface EmbeddedResponse<T> {
    _embedded: Record<string, T[]>;
    _links: Record<string, Link>;
}

export interface GenericResponse<T> {
    response: T;
}


export const fetchLink = async <T>(link: Link, body: any = undefined): Promise<T> => {
    try {
        const options: RequestInit = {
            method: link.type,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${sessionStorage.getItem('token')}`,
            },
        };

        if (body && (link.type === 'POST' || link.type === 'PUT')) {
            options.body = JSON.stringify(body);
        }

        const response = await fetch(link.href, options);

        const data = await response.text();

        if (response.ok) {
            return JSON.parse(data);
        } else {
            throw new Error( JSON.parse(data).message );
        }

    } catch (error) {
        throw error;
    }
};


