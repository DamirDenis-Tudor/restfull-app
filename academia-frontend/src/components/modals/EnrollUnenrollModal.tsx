import React from 'react';
import {Modal, Button, Form} from 'react-bootstrap';
import {Link} from "../../api/hateoas.ts";
import {StudentList} from "../lists/StudentList.tsx";

interface EnrollUnenrollModalProps {
    show: boolean;
    isEnrolling: boolean;
    studentIds: string;
    studentLink: Link;
    onStudentIdsChange: (value: string) => void;
    handleClose: () => void;
    handleAction: () => void;
}

export const EnrollUnenrollModal: React.FC<EnrollUnenrollModalProps> = (
    {
        show,
        isEnrolling,
        studentIds,
        studentLink,
        onStudentIdsChange,
        handleClose,
        handleAction,
    }) => {
    const onClickOverride = (studentId: number, isActive: boolean) => {
        const idList = studentIds.split(',').map(id => id.trim());
        const studentIdStr = studentId.toString();

        if (isActive && !idList.includes(studentIdStr)) {
            idList.push(studentIdStr);
        } else if (!isActive) {
            const index = idList.indexOf(studentIdStr);
            if (index > -1) idList.splice(index, 1);
        }

        onStudentIdsChange(idList.join(', '));
    };

    return (
        <Modal show={show} onHide={handleClose} centered size={"xl"} style={{transform: 'scale(0.8)', transformOrigin: 'top'}}>
            <Modal.Header closeButton>
                <Modal.Title>{isEnrolling ? 'Enroll Students' : 'Unenroll Students'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="studentIds">
                        <Form.Label>Enter Student IDs (comma separated)</Form.Label>
                        <Form.Control
                            type="text"
                            value={studentIds}
                            readOnly
                            placeholder="e.g., 1, 2, 3"
                        />
                            <StudentList
                                link={studentLink}
                                onClickOverride={onClickOverride}
                            />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Close
                </Button>
                <Button variant="primary" onClick={handleAction}>
                    {isEnrolling ? 'Enroll' : 'Unenroll'}
                </Button>
            </Modal.Footer>
        </Modal>
    );
};