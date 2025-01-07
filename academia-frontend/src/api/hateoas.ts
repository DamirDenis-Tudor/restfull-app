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
    uploaded_at: string;
    size: number;
}

export interface FileInfo {
    fileMetadata: FileMetadata;
    _links: Link[];
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

export interface EmbeddedResponse<T> {
    _embedded: Record<string, T[]>;
    _links: Record<string, Link>;
}

export interface GenericResponse<T> {
    response: T;
}


export const fetchComponentData = async <T>(link: Link): Promise<T> => {
    try {
        console.log(link.href);
        const response = await fetch(link.href, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${sessionStorage.getItem('token')}`,
            },
        });

        const data = await response.text();
       // console.log(data);
        if (response.ok) {
            return JSON.parse(data);
        }
    } catch (error) {
        console.error("Fetch error:", error);
        throw error;
    }

    throw Error();
};
