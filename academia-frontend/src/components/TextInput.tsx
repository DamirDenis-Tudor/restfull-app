import {Form} from 'react-bootstrap';
import React, {useState} from "react";

interface TextInputComponentProps {
    className?: string;
    label?: string;
    id: string;
    type?: 'text' | 'password' | 'email' | 'number' | 'tel' | 'url';
    helpText?: string;
    ariaDescribedby?: string;
    placeholder?: string;
    value: string;
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
    isValid?: boolean;
    errorMessage?: string;
}

export function TextInput(
    {
        className,
        label,
        id,
        type = 'text',
        helpText,
        ariaDescribedby,
        placeholder,
        value,
        onChange,
        isValid = true,
        errorMessage,
    }: TextInputComponentProps) {
    const [isFocused, setIsFocused] = useState(false);

    const handleFocus = () => setIsFocused(true);
    const handleBlur = () => setIsFocused(false);

    return (
        <div className="mb-3">
            {label && <Form.Label htmlFor={id}>{label}</Form.Label>}
            <Form.Control
                className={className}
                type={type}
                id={id}
                value={value}
                onChange={onChange}
                aria-describedby={ariaDescribedby}
                placeholder={placeholder}
                onFocus={handleFocus}
                onBlur={handleBlur}
                isInvalid={!isValid}
            />
            {isFocused && helpText && (
                <Form.Text id={ariaDescribedby} muted>
                    {helpText}
                </Form.Text>
            )}
            {!isValid && errorMessage && (
                <Form.Control.Feedback type="invalid">
                    {errorMessage}
                </Form.Control.Feedback>
            )}
        </div>
    );
}
