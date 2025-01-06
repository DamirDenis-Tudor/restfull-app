import {Form} from 'react-bootstrap';
import {useState} from "react";

interface TextInputComponentProps {
    className?: string;
    label?: string;
    id: string;
    type?: 'text' | 'password' | 'email' | 'number' | 'tel' | 'url';
    helpText?: string;
    ariaDescribedby?: string;
    placeholder?: string;
}

export function TextInputComponent(
    {
        className,
        label,
        id,
        type = 'text',
        helpText,
        ariaDescribedby,
        placeholder,
    }: TextInputComponentProps) {
    const [isFocused, setIsFocused] = useState(false);

    const handleFocus = () => setIsFocused(true);
    const handleBlur = () => setIsFocused(false);

    return (
        <>
            <Form.Label htmlFor={id}>{label}</Form.Label>
            <Form.Control
                className={className}
                type={type}
                id={id}
                aria-describedby={ariaDescribedby}
                placeholder={placeholder}
                onFocus={handleFocus}
                onBlur={handleBlur}
            />
            {isFocused && helpText && (
                <Form.Text id={ariaDescribedby} muted>
                    {helpText}
                </Form.Text>
            )}
        </>
    );
}
